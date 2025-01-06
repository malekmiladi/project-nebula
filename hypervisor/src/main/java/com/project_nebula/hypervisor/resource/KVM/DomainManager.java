package com.project_nebula.hypervisor.resource.KVM;

import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;
import com.project_nebula.hypervisor.resource.VirtualMachineState;
import com.project_nebula.hypervisor.utils.XMLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainInterface;
import org.libvirt.LibvirtException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
public class DomainManager {
    private final Connect hypervisorConn;

    public DomainManager(Connect hypervisorConn) {
        this.hypervisorConn = hypervisorConn;
    }

    public String createDomainXMLDescription(String id, VirtualMachineSpecs specs, String cloudDataSource) throws ParserConfigurationException, TransformerException, NullPointerException {
        XMLBuilder builder = new XMLBuilder("domain");
        return builder
                .setAttribute("type", "kvm")
                .addChild("name")
                .setText(id)
                .stepBack(1)
                .addChild("vRam")
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
                .setText("ds=nocloud;s=" + cloudDataSource)
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
                .setAttribute("file", "/var/lib/libvirt/images/" + id + ".qcow2")
                .stepBack(1)
                .addChild("target")
                .setAttribute("dev", "vda")
                .setAttribute("bus", "virtio")
                .stepBack(2)
                .addChild("interface")
                .setAttribute("type", "network")
                .addChild("source")
                .setAttribute("network", "default")
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

    public Domain createDomain(String id, VirtualMachineSpecs specs, String cloudDataSource) throws Exception {
        try {
            String domainXMLDescription = createDomainXMLDescription(id, specs, cloudDataSource);
            Domain domain = hypervisorConn.domainDefineXML(domainXMLDescription);
            domain.setAutostart(true);
            domain.create();
            return domain;
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to create domain with id:\"{0}\".\n{1}", id, e.getMessage()), e);
        }
    }

    public HashMap<String, String> awaitForIpAssignment(Domain domain) throws Exception {
        HashMap<String, String> ipAddresses = new HashMap<>();
        Collection<DomainInterface> interfaces = new ArrayList<>();
        do {
            try {
                interfaces = domain.interfaceAddresses(Domain.InterfaceAddressesSource.VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_LEASE, 0);
            } catch (Exception e) {
                throw new Exception(MessageFormat.format("Failed to fetch interfaces for domain \"{0}\".\n{1}", domain.getName(), e.getMessage()), e);
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

    public VirtualMachineState getDomainState(Domain domain) throws Exception {
        try {
            return switch (domain.getInfo().state) {
                case VIR_DOMAIN_RUNNING -> VirtualMachineState.RUNNING;
                case VIR_DOMAIN_PAUSED -> VirtualMachineState.STOPPED;
                case VIR_DOMAIN_SHUTDOWN -> VirtualMachineState.SHUTDOWN;
                case VIR_DOMAIN_CRASHED -> VirtualMachineState.CRASHED;
                default -> VirtualMachineState.UNKNOWN;
            };
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to get domain state for domain \"{0}\".", domain.getName()), e);
        }
    }

    public VirtualMachineState getDomainStateById(String id) throws Exception {
        try {
            Domain domain = hypervisorConn.domainLookupByName(id);
            return getDomainState(domain);
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to get domain state for domain \"{0}\".", id), e);
        }
    }

    public void deleteDomain(Domain domain) throws Exception {
        try {
            domain.destroy();
            domain.undefine();
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to delete domain \"{0}\".", domain.getName()), e);
        }
    }

    public void deleteDomain(String id) throws Exception {
        try {
            Domain domain = hypervisorConn.domainLookupByName(id);
            deleteDomain(domain);
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to delete domain \"{0}\".", id), e);
        }
    }

    public void shutdownDomain(Domain domain) throws Exception {
        try {
            if (domain.isActive() == 1) {
                shutdownDomain(domain);
            }
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to shutdown domain \"{0}\"", domain.getName()), e);
        }
    }
    public void shutdownDomain(String id) throws Exception {
        Domain domain = null;
        try {
            domain = hypervisorConn.domainLookupByName(id);
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Domain \"{0}\" may not exist.", id), e);
        }
        shutdownDomain(domain);
    }
}
