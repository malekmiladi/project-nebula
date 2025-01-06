package com.project_nebula.hypervisor.resource.KVM;

import com.project_nebula.hypervisor.resource.VirtualMachineMetadata;
import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;
import com.project_nebula.hypervisor.resource.VirtualMachineState;
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
            return new Connect(hypervisorUri);
        } catch (LibvirtException exception) {
            log.error("Failed to connect to hypervisor: {}", exception.getMessage());
            throw new Exception("Failed to connect to hypervisor. Please verify that your system supports KVM virtualization.");
        }
    }

    public Result<VirtualMachineMetadata> createVirtualMachine(String id, VirtualMachineSpecs specs, String cloudDatasource) {
        StorageVol newVolume = null;
        Domain newDomain = null;
        HashMap<String, String> ipAddresses = null;
        VirtualMachineState state = null;
        try {
            newVolume = storageManager.createVolume(id, specs.getVDiskGb());
            storageManager.uploadImageToVolume(
                    specs.getImage().getSource(),
                    specs.getImage().getUrl(),
                    HYPERVISOR_CONNECTION.streamNew(0),
                    newVolume
            );
            newDomain = domainManager.createDomain(id, specs, cloudDatasource);
            ipAddresses = domainManager.awaitForIpAssignment(newDomain);
            state = domainManager.getDomainState(newDomain);
            return Result.success(new VirtualMachineMetadata(state, ipAddresses));
        } catch (Exception createException) {
            try {
                cleanupResources(id, state, newDomain, newVolume);
            } catch (Exception cleanupException) {
                createException.addSuppressed(cleanupException);
            }
            return Result.failure(createException);
        }
    }

    private void cleanupResources(String id, VirtualMachineState state, Domain domain, StorageVol volume) throws Exception {
        Exception cleanupException = null;
        if (state != null || domain != null) {
            try {
                domainManager.shutdownDomain(domain);
            } catch (Exception e) {
                cleanupException = e;
            }
        }
        if (volume != null) {
            try {
                storageManager.deleteVolume(id);
            } catch (Exception e) {
                if (cleanupException == null) {
                    cleanupException = e;
                } else {
                    cleanupException.addSuppressed(e);
                }
            }
        }
        if (state != null || domain != null) {
            domainManager.deleteDomain(domain);
        }
        if (cleanupException != null) {
            throw cleanupException;
        }
    }

    public Result<Boolean> deleteVirtualMachine(String id) {
        try {
            domainManager.shutdownDomain(id);
            storageManager.deleteVolume(id);
            domainManager.deleteDomain(id);
            return Result.success(true);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> shutdownVirtualMachine(String id) {
        try {
            domainManager.shutdownDomain(id);
            return Result.success(true);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

}

