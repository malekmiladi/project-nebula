package com.project_nebula.hypervisor.resource.kvm;

import com.project_nebula.hypervisor.resource.kvm.exceptions.*;
import com.project_nebula.hypervisor.utils.XMLBuilder;
import com.project_nebula.shared.resource.VirtualMachineSpecs;
import com.project_nebula.shared.resource.VirtualMachineState;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.Domain.RebootFlags;
import org.libvirt.DomainInterface;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
public class DomainManager {

    private final Connect hypervisorConn;
    private final String defaultStoragePoolLocation;
    private final String networkName;
    private final int INTERFACE_FETCH_MAX_TRIES = 15;

    public DomainManager(Connect hypervisorConn, String defaultStoragePoolLocation, String networkName) {
        this.hypervisorConn = hypervisorConn;
        this.defaultStoragePoolLocation = defaultStoragePoolLocation;
        this.networkName = networkName;
    }

    public String createDomainXMLDescription(String id, VirtualMachineSpecs specs, String cloudDataSource) throws ParserConfigurationException, TransformerException, NullPointerException {
        XMLBuilder builder = new XMLBuilder("domain");
        return builder
                .setAttribute("type", "kvm")
                .addChild("name")
                .setText(id)
                .stepBack(1)
                .addChild("memory")
                .setAttribute("unit", "G")
                .setText(String.valueOf(specs.getVRamGb()))
                .stepBack(1)
                .addChild("currentMemory")
                .setAttribute("unit", "G")
                .setText(String.valueOf(specs.getVRamGb()))
                .stepBack(1)
                .addChild("vcpu")
                .setAttribute("placement", "static")
                .setText(String.valueOf(specs.getVCpus()))
                .stepBack(1)
                .addChild("sysinfo")
                .setAttribute("type", "smbios")
                .addChild("system")
                .addChild("entry")
                .setAttribute("name", "serial")
                .setText("ds=nocloud;s=" + cloudDataSource + id + "/")
                .stepBack(3)
                .addChild("os")
                .addChild("type")
                .setText("hvm")
                .stepBack(1)
                .addChild("boot")
                .setAttribute("dev", "hd")
                .stepBack(1)
                .addChild("smbios")
                .setAttribute("node", "sysinfo")
                .stepBack(2)
                .addChild("cpu")
                .setAttribute("mode", "host-passthrough")
                .stepBack(1)
                .addChild("on_reboot")
                .setText("restart")
                .stepBack(1)
                .addChild("on_crash")
                .setText("restart")
                .stepBack(1)
                .addChild("devices")
                .addChild("emulator")
                .setText("/usr/bin/qemu-system-x86_64")
                .stepBack(1)
                .addChild("disk")
                .setAttribute("type", "file")
                .setAttribute("device", "disk")
                .addChild("driver")
                .setAttribute("name", "qemu")
                .setAttribute("type", "qcow2")
                .stepBack(1)
                .addChild("source")
                .setAttribute("file", defaultStoragePoolLocation + "/" + id + ".qcow2")
                .stepBack(1)
                .addChild("target")
                .setAttribute("dev", "vda")
                .setAttribute("bus", "virtio")
                .stepBack(2)
                .addChild("interface")
                .setAttribute("type", "network")
                .addChild("source")
                .setAttribute("network", networkName)
                .stepBack(1)
                .addChild("model")
                .setAttribute("type", "virtio")
                .stepBack(2)
                .addChild("serial")
                .setAttribute("type", "pty")
                .addChild("source")
                .setAttribute("path", "/dev/pts/6")
                .stepBack(1)
                .addChild("target")
                .setAttribute("type", "isa-serial")
                .setAttribute("port", "0")
                .addChild("model")
                .setAttribute("name", "isa-serial")
                .stepBack(3)
                .addChild("console")
                .setAttribute("type", "pty")
                .addChild("target")
                .setAttribute("type", "serial")
                .stepBack(1)
                .addChild("address")
                .setAttribute("type", "virtio-serial")
                .setAttribute("controller", "0")
                .setAttribute("bus", "0")
                .setAttribute("port", "1")
                .stepBack(2)
                .addChild("input")
                .setAttribute("type", "mouse")
                .setAttribute("bus", "ps2")
                .stepBack(1)
                .addChild("input")
                .setAttribute("type", "keyboard")
                .setAttribute("bus", "ps2")
                .build();
    }

    public Domain createDomain(String id, VirtualMachineSpecs specs, String cloudDataSource) throws DomainCreateException {
        try {
            String domainXMLDescription = createDomainXMLDescription(id, specs, cloudDataSource);
            Domain domain = hypervisorConn.domainDefineXML(domainXMLDescription);
            domain.setAutostart(true);
            domain.create();
            return domain;
        } catch (Exception e) {
            throw new DomainCreateException(MessageFormat.format("Failed to create domain ({0}). {1}", id, e.getMessage()), e);
        }
    }

    public HashMap<String, String> awaitForIpAssignment(Domain domain) throws DomainInterfaceException {
        HashMap<String, String> ipAddresses = new HashMap<>();
        Collection<DomainInterface> interfaces;
        do {
            try {
                interfaces = domain.interfaceAddresses(Domain.InterfaceAddressesSource.VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_LEASE, 0);
            } catch (Exception e) {
                throw new DomainInterfaceException(MessageFormat.format("Failed to fetch interfaces for domain \"{0}\".\n{1}", getDomainName(domain), e.getMessage()), e);
            }
        } while (interfaces.isEmpty());
        for (DomainInterface domainInterface : interfaces) {
            for (DomainInterface.InterfaceAddress addr : domainInterface.addrs) {
                if (addr.address instanceof Inet4Address) {
                    ipAddresses.put("ipv4", addr.address.getHostAddress());
                } else if (addr.address instanceof Inet6Address) {
                    ipAddresses.put("ipv6", addr.address.getHostAddress());
                }
            }
        }
        return ipAddresses;
    }

    public Domain getDomainById(String id) throws DomainNotFoundException {
        try {
            return hypervisorConn.domainLookupByName(id);
        } catch (Exception e) {
            throw new DomainNotFoundException(MessageFormat.format("Domain \"{0}\" may not exist.", id), e);
        }
    }

    public VirtualMachineState getDomainState(Domain domain) throws RuntimeException {
        try {
            return switch (domain.getInfo().state) {
                case VIR_DOMAIN_RUNNING -> VirtualMachineState.RUNNING;
                case VIR_DOMAIN_PAUSED -> VirtualMachineState.STOPPED;
                case VIR_DOMAIN_SHUTDOWN -> VirtualMachineState.SHUTDOWN;
                case VIR_DOMAIN_CRASHED -> VirtualMachineState.CRASHED;
                default -> VirtualMachineState.UNKNOWN;
            };
        } catch (Exception e) {
            throw new RuntimeException(MessageFormat.format("Failed to get domain state for domain \"{0}\".", getDomainName(domain)), e);
        }
    }

    public VirtualMachineState getDomainStateById(String id) throws RuntimeException {
        try {
            Domain domain = getDomainById(id);
            return getDomainState(domain);
        } catch (Exception e) {
            throw new RuntimeException(MessageFormat.format("Failed to get domain state for domain \"{0}\".", id), e);
        }
    }

    public void deleteDomain(Domain domain) throws DomainDeleteException {
        try {
            domain.getName();
            shutdownDomain(domain, true);
            domain.undefine();
        } catch (Exception e) {
            throw new DomainDeleteException(MessageFormat.format("Failed to delete domain \"{0}\".", getDomainName(domain)), e);
        }
    }

    public void shutdownDomain(Domain domain, boolean force) throws DomainStopException {
        try {
            if (domain.isActive() == 1) {
                if (force) {
                    destroyDomain(domain);
                }
                domain.shutdown();
            }
        } catch (Exception e) {
            throw new DomainStopException(MessageFormat.format("Failed to shutdown domain \"{0}\"", getDomainName(domain)), e);
        }
    }

    public void destroyDomain(Domain domain) {
        try {
            domain.destroy();
        } catch (Exception e) {
            throw new DomainStopException(MessageFormat.format("Failed to force shutdown domain \"{0}\"", getDomainName(domain)), e);
        }
    }

    public void shutdownDomain(String id) throws DomainStopException {
        Domain domain = getDomainById(id);
        shutdownDomain(domain, false);
    }

    public void restartDomain(Domain domain) throws DomainRebootException {
        try {
            domain.reboot(RebootFlags.DEFAULT);
        } catch (Exception e) {
            throw new DomainRebootException(MessageFormat.format("Failed to reboot domain \"{0}\".", getDomainName(domain)), e);
        }
    }

    public Domain restartDomain(String id) throws DomainRebootException {
        Domain domain = getDomainById(id);
        restartDomain(domain);
        return domain;
    }

    public String getDomainName(Domain domain) throws DomainNotFoundException {
        try {
            return domain.getName();
        } catch (Exception e) {
            throw new DomainNotFoundException("Can't retrieve domain name.");
        }
    }

}
