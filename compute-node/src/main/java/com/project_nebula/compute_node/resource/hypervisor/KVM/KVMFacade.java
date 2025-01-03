package com.project_nebula.compute_node.resource.hypervisor.KVM;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.resource.VirtualMachineMetadata;
import com.project_nebula.compute_node.resource.VirtualMachineState;
import com.project_nebula.compute_node.resource.hypervisor.VirtualMachineSpecs;
import com.project_nebula.compute_node.resource.image.*;
import com.project_nebula.compute_node.utils.ApplicationControl;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.*;

import java.util.HashMap;

@Slf4j
public class KVMFacade {

    private final Connect HYPERVISOR_CONNECTION;

    private final DomainManager domainManager;
    private final StorageManager storageManager;
    private final NetworkManager networkManager;

    public KVMFacade() {
        HYPERVISOR_CONNECTION = connectToHypervisor(ComputeConfiguration.HYPER_VISOR_CONNECTION_URI);
        storageManager = new StorageManager(HYPERVISOR_CONNECTION);
        domainManager = new DomainManager(HYPERVISOR_CONNECTION);
        networkManager = new NetworkManager(HYPERVISOR_CONNECTION);
    }

    public Connect connectToHypervisor(String hypervisorUri) {
        try {
            return new Connect(hypervisorUri);
        } catch (LibvirtException exception) {
            log.error("Failed to connect to hypervisor: {}", exception.getMessage());
            ApplicationControl.exit(1);
        }
        return null;
    }

    public VirtualMachineMetadata createVirtualMachine(String id, VirtualMachineSpecs specs) {
        ImageDownloadStrategy downloadStrategy = specs.getImage().getSource() == ImageSource.LOCAL ?
                new LocalImageStrategy() :
                new OnlineImageStrategy();
        ImageProvider imageProvider = new ImageProvider(downloadStrategy);
        StorageVol newVolume = null;
        Domain newVm;
        HashMap<String, String> ipAddresses;
        VirtualMachineState state = null;
        try {
            newVolume = storageManager.createVolume(id, specs.getVDiskGb());
        } catch (Exception e) {
            return null;
        }

        try {
            Stream fileStream = HYPERVISOR_CONNECTION.streamNew(0);
            imageProvider.writeImageToVolume(specs.getImage().getUrl(), fileStream, newVolume);
        } catch (Exception e) {
            return null;
        }

        try {
            newVm = domainManager.createDomain(id, specs);
            ipAddresses = domainManager.awaitForIpAssignment(newVm);
        } catch (Exception e) {
            return null;
        }

        try {
            return new VirtualMachineMetadata(domainManager.getDomainState(newVm), ipAddresses);
        } catch (Exception e) {
            return null;
        }
    }

}
