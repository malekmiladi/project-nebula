package com.project_nebula.hypervisor.resource.kvm;

import com.project_nebula.hypervisor.utils.Result;
import com.project_nebula.shared.resource.VirtualMachineMetadata;
import com.project_nebula.shared.resource.VirtualMachineSpecs;
import com.project_nebula.shared.resource.VirtualMachineState;
import com.project_nebula.shared.resource.image.ImageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.*;
import org.libvirt.Stream;

import java.util.HashMap;

@Slf4j
public class KVMFacade {

    private final Connect HYPERVISOR_CONNECTION;

    private final DomainManager domainManager;
    private final StorageManager storageManager;
    private final NetworkManager networkManager;

    public KVMFacade(String hyperVisorConnectionUri, String defaultStoragePoolName, String defaultStoragePoolLocation, String networkName) throws Exception {
        HYPERVISOR_CONNECTION = connectToHypervisor(hyperVisorConnectionUri);
        storageManager = new StorageManager(HYPERVISOR_CONNECTION, defaultStoragePoolName, defaultStoragePoolLocation);
        domainManager = new DomainManager(HYPERVISOR_CONNECTION, defaultStoragePoolLocation, networkName);
        networkManager = new NetworkManager(HYPERVISOR_CONNECTION);
    }

    public Connect connectToHypervisor(String hypervisorUri) throws Exception {
        try {
            Connect conn = new Connect(hypervisorUri);
            log.info("Connected to Hypervisor at \"{}\"", hypervisorUri);
            return conn;
        } catch (LibvirtException exception) {
            log.error("Failed to connect to hypervisor at \"{}\": {}", hypervisorUri, exception.getMessage());
            throw new Exception("Failed to connect to hypervisor. Please verify that your system supports KVM virtualization.");
        }
    }

    public Result<VirtualMachineMetadata> createVirtualMachine(String id, VirtualMachineSpecs specs, ImageMetadata image, String cloudDatasource) {
        log.info("Creating virtual machine { id: {}, cpus: {}, memory: {}GB, disk: {}GB }", id, specs.getVCpus(), specs.getVRamGb(), specs.getVDiskGb());
        StorageVol newVolume = null;
        Domain newDomain = null;
        HashMap<String, String> ipAddresses = null;
        VirtualMachineState state = null;
        try {
            newVolume = storageManager.createVolume(id, specs.getVDiskGb());
            storageManager.uploadImageToVolume(
                    image.getSource(),
                    image.getUrl(),
                    HYPERVISOR_CONNECTION.streamNew(Stream.VIR_STREAM_NONBLOCK),
                    newVolume
            );
            newDomain = domainManager.createDomain(id, specs, cloudDatasource);
            ipAddresses = domainManager.awaitForIpAssignment(newDomain);
            state = domainManager.getDomainState(newDomain);
            return Result.success(new VirtualMachineMetadata(state, ipAddresses));
        } catch (Exception createException) {
            try {
                log.error("Failed to create Domain {}. Deleting domain {} and volume {}.qcow2", id, id, id);
                cleanupResources(id);
            } catch (Exception cleanupException) {
                createException.addSuppressed(cleanupException);
            }
            return Result.failure(createException);
        }
    }

    private void cleanupResources(String id) throws Exception {
        Exception cleanupException = null;
        Domain domain = null;
        StorageVol volume = null;

        try {
            domain = domainManager.getDomainById(id);
        } catch (Exception e) {
            cleanupException = e;
        }

        try {
            volume = storageManager.getVolumeById(id);
        } catch (Exception e) {
            if (cleanupException == null) {
                cleanupException = e;
            } else {
                cleanupException.addSuppressed(e);
            }
        }

        if (domain != null) {
            try {
                domainManager.shutdownDomain(domain, true);
            } catch (Exception e) {
                if (cleanupException == null) {
                    cleanupException = e;
                } else {
                    cleanupException.addSuppressed(e);
                }
            }
        }

        if (volume != null) {
            try {
                storageManager.deleteVolume(volume);
            } catch (Exception e) {
                if (cleanupException == null) {
                    cleanupException = e;
                } else {
                    cleanupException.addSuppressed(e);
                }
            }
        }

        if (domain != null) {
            try {
                domainManager.deleteDomain(domain);
            } catch (Exception e) {
                if (cleanupException == null) {
                    cleanupException = e;
                } else {
                    cleanupException.addSuppressed(e);
                }
            }
        }

        if (cleanupException != null) {
            throw cleanupException;
        }

    }

    private void cleanupResources(String id, Domain domain, StorageVol volume) throws Exception {
        Exception cleanupException = null;
        if (domain != null) {
            try {
                log.info("Shutting down Domain \"{}\"", id);
                domainManager.shutdownDomain(domain, true);
            } catch (Exception e) {
                log.error("Failed to shut down Domain \"{}\"", id);
                cleanupException = e;
            }
        }
        if (volume != null) {
            try {
                log.info("Deleting volume \"{}\".qcow2", id);
                storageManager.deleteVolume(id);
            } catch (Exception e) {
                log.error("Failed to delete volume \"{}\".qcow2", id);
                if (cleanupException == null) {
                    cleanupException = e;
                } else {
                    cleanupException.addSuppressed(e);
                }
            }
        }
        if (domain != null) {
            try {
                log.info("Deleting Domain \"{}\"", id);
                domainManager.deleteDomain(domain);
            } catch (Exception e) {
                log.info("Failed to delete Domain \"{}\"", id);
                if (cleanupException == null) {
                    cleanupException = e;
                } else {
                    cleanupException.addSuppressed(e);
                }
            }
        }

        if (cleanupException != null) {
            throw cleanupException;
        }
    }

    public Result<VirtualMachineMetadata> deleteVirtualMachine(String id) {
        try {
            log.info("Deleting Domain {}", id);
            Domain domain = domainManager.getDomainById(id);
            domainManager.shutdownDomain(domain, true);
            storageManager.deleteVolume(id);
            domainManager.deleteDomain(domain);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Failed to delete Domain {}", id);
            return Result.failure(e);
        }
    }

    public Result<VirtualMachineMetadata> stopVirtualMachine(String id) {
        try {
            log.info("Stopping Domain {}", id);
            domainManager.shutdownDomain(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Failed to stop Domain {}", id);
            return Result.failure(e);
        }
    }

    public Result<VirtualMachineMetadata> restartVirtualMachine(String id) {
        try {
            log.info("Restarting Domain {}", id);
            Domain domain = domainManager.restartDomain(id);
            HashMap<String, String> ipAddresses = domainManager.awaitForIpAssignment(domain);
            VirtualMachineState state = domainManager.getDomainState(domain);
            return Result.success(new VirtualMachineMetadata(state, ipAddresses));
        } catch (Exception e) {
            log.error("Failed to restart Domain {}", id);
            return Result.failure(e);
        }
    }

}

