package com.project_nebula.compute_node.api;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.utils.ApplicationControl;
import com.project_nebula.hypervisor.HypervisorInterface;
import com.project_nebula.hypervisor.resource.kvm.KVMFacade;
import com.project_nebula.shared.resource.VirtualMachineMetadata;
import com.project_nebula.shared.resource.VirtualMachineSpecs;
import com.project_nebula.shared.resource.image.ImageMetadata;
import com.project_nebula.shared.utils.Result;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KVMInterface implements HypervisorInterface {

    private KVMFacade facade;

    public KVMInterface(ComputeConfiguration conf) {
        try {
            this.facade = new KVMFacade(conf.getHypervisorConnectionURI(), conf.getStoragePoolName(), conf.getStoragePoolLocation(), conf.getNetworkName());
        } catch (Exception e) {
            log.error("An error occurred while initializing KVM environment:\n{}", e.getMessage());
            ApplicationControl.exit(1);
        }
    }

    public Result<VirtualMachineMetadata> createVM(String id, VirtualMachineSpecs specs, ImageMetadata metadata, String cloudDataSourceUrl) {
        return facade.createVirtualMachine(id, specs, metadata, cloudDataSourceUrl);
    }

    public Result<VirtualMachineMetadata> startVM(String id) {
        return facade.restartVirtualMachine(id);
    }

    public Result<VirtualMachineMetadata> restartVM(String id) {
        return facade.restartVirtualMachine(id);
    }

    public Result<VirtualMachineMetadata> stopVM(String id) {
        return facade.stopVirtualMachine(id);
    }

    public Result<VirtualMachineMetadata> deleteVM(String id) {
        return facade.deleteVirtualMachine(id);
    }

}
