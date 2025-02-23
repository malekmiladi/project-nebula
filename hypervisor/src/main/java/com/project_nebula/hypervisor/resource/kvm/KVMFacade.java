package com.project_nebula.hypervisor.resource.kvm;

import com.project_nebula.shared.resource.VirtualMachineError;
import com.project_nebula.shared.resource.*;
import com.project_nebula.shared.resource.image.ImageMetadata;
import com.project_nebula.shared.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.Domain;
import org.libvirt.StorageVol;

import java.util.HashMap;

@Slf4j
public class KVMFacade {

    private final ConnectionProvider connectionProvider;
    private final DomainManager domainManager;
    private final StorageManager storageManager;
    private final NetworkManager networkManager;

    public KVMFacade(String hyperVisorConnectionUri, String defaultStoragePoolName, String defaultStoragePoolLocation, String networkName) throws Exception {
        connectionProvider = ConnectionProvider.getInstance(hyperVisorConnectionUri);
        storageManager = new StorageManager(connectionProvider.getConnection(), defaultStoragePoolName, defaultStoragePoolLocation);
        domainManager = new DomainManager(connectionProvider.getConnection(), defaultStoragePoolLocation, networkName);
        networkManager = new NetworkManager(connectionProvider.getConnection());
    }

    public boolean isOperational() {
        try {
            return connectionProvider.getConnection().isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public Result<VirtualMachineMetadata> createVirtualMachine(String id, VirtualMachineSpecs specs, ImageMetadata image, String cloudDatasource) {
        log.info("Creating virtual machine { id: {}, cpus: {}, memory: {}GB, disk: {}GB }", id, specs.getCpus(), specs.getMemory(), specs.getDisk());
        StorageVol newVolume;
        Domain newDomain;
        HashMap<String, String> ipAddresses;
        VirtualMachineState state;
        try {
            newVolume = storageManager.createVolume(id, specs.getDisk());
            storageManager.uploadImageToVolume(
                    image.getSource(),
                    image.getUrl(),
                    connectionProvider.getNewStream(),
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
            VirtualMachineError error = VirtualMachineError.builder()
                    .message(createException.getMessage())
                    .type(VirtualMachineErrorType.CREATE_ERROR)
                    .build();
            return Result.failure(error);
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
            VirtualMachineError error = VirtualMachineError.builder()
                    .message(e.getMessage())
                    .type(VirtualMachineErrorType.DELETE_ERROR)
                    .build();
            return Result.failure(error);
        }
    }

    public Result<VirtualMachineMetadata> stopVirtualMachine(String id) {
        try {
            log.info("Stopping Domain {}", id);
            domainManager.shutdownDomain(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Failed to stop Domain {}", id);
            VirtualMachineError error = VirtualMachineError.builder()
                    .message(e.getMessage())
                    .type(VirtualMachineErrorType.STOP_ERROR)
                    .build();
            return Result.failure(error);
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
            VirtualMachineError error = VirtualMachineError.builder()
                    .message(e.getMessage())
                    .type(VirtualMachineErrorType.RESTART_ERROR)
                    .build();
            return Result.failure(error);
        }
    }

}

