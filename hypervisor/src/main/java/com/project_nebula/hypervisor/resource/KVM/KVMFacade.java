package com.project_nebula.hypervisor.resource.KVM;

import com.project_nebula.hypervisor.resource.VirtualMachineMetadata;
import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;
import com.project_nebula.hypervisor.resource.VirtualMachineState;
import com.project_nebula.hypervisor.resource.image.ImageMetadata;
import com.project_nebula.hypervisor.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.*;

import java.text.MessageFormat;
import java.util.HashMap;

@Slf4j
public class KVMFacade {

    private final Connect HYPERVISOR_CONNECTION;

    private final DomainManager domainManager;
    private final StorageManager storageManager;
    private final NetworkManager networkManager;

    public KVMFacade(String hyperVisorConnectionUri, String defaultStoragePoolName) throws Exception {
        HYPERVISOR_CONNECTION = connectToHypervisor(hyperVisorConnectionUri);
        storageManager = new StorageManager(HYPERVISOR_CONNECTION, defaultStoragePoolName);
        domainManager = new DomainManager(HYPERVISOR_CONNECTION);
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
        log.info("Creating virtual machine with id \"{}\" and specs [{} vCPUs] [{} vMEMORY] [{} vDISK]", id, specs.getVCpus(), specs.getVRamGb(), specs.getVDiskGb());
        StorageVol newVolume = null;
        Domain newDomain = null;
        HashMap<String, String> ipAddresses = null;
        VirtualMachineState state = null;
        try {
            newVolume = storageManager.createVolume(id, specs.getVDiskGb());
            storageManager.uploadImageToVolume(
                    image.getSource(),
                    image.getUrl(),
                    HYPERVISOR_CONNECTION.streamNew(0),
                    newVolume
            );
            newDomain = domainManager.createDomain(id, specs, cloudDatasource);
            ipAddresses = domainManager.awaitForIpAssignment(newDomain);
            state = domainManager.getDomainState(newDomain);
            return Result.success(new VirtualMachineMetadata(state, ipAddresses));
        } catch (Exception createException) {
            try {
                log.info("Failed to create Domain with id {}.\n Deleting domain {} and volume {}.qcow2", id, id, id);
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
                domainManager.shutdownDomain(domain);
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
                domainManager.shutdownDomain(domain);
            } catch (Exception e) {
                log.info("Failed to shut down Domain \"{}\"", id);
                cleanupException = e;
            }
        }
        if (volume != null) {
            try {
                log.info("Deleting volume \"{}\".qcow2", id);
                storageManager.deleteVolume(id);
            } catch (Exception e) {
                log.info("Failed to delete volume \"{}\".qcow2", id);
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
            domainManager.shutdownDomain(id);
            storageManager.deleteVolume(id);
            domainManager.deleteDomain(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<VirtualMachineMetadata> stopVirtualMachine(String id) {
        try {
            domainManager.shutdownDomain(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<VirtualMachineMetadata> restartVirtualMachine(String id) {
        try {
            Domain domain = domainManager.restartDomain(id);
            HashMap<String, String> ipAddresses = domainManager.awaitForIpAssignment(domain);
            VirtualMachineState state = domainManager.getDomainState(domain);
            return Result.success(new VirtualMachineMetadata(state, ipAddresses));
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

}

