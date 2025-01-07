package com.project_nebula.compute_node.api;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.utils.ApplicationControl;
import com.project_nebula.hypervisor.HypervisorInterface;
import com.project_nebula.hypervisor.resource.KVM.KVMFacade;
import com.project_nebula.hypervisor.resource.VirtualMachineMetadata;
import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;
import com.project_nebula.hypervisor.resource.image.ImageMetadata;
import com.project_nebula.hypervisor.utils.Result;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KVMInterface implements HypervisorInterface {

    private KVMFacade facade;

    public KVMInterface(ComputeConfiguration conf) {
        try {
            this.facade = new KVMFacade(conf.getHyperVisorConnectionURI(), conf.getStoragePoolName());
        } catch (Exception e) {
            log.error("An error occurred while initializing KVM environment:\n{}", e.getMessage());
            ApplicationControl.exit(1);
        }
    }

    @Override
    public Result<VirtualMachineMetadata> createVM(String id, VirtualMachineSpecs specs, ImageMetadata metadata, String cloudDataSourceUrl) {
        return facade.createVirtualMachine(id, specs, metadata, cloudDataSourceUrl);
    }

    @Override
    public Result<VirtualMachineMetadata> startVM(String id) {
        return facade.startVirtualMachine(id);
    }

    @Override
    public Result<VirtualMachineMetadata> restartVM(String id) {
        return facade.restartVirtualMachine(id);
    }

    @Override
    public Result<VirtualMachineMetadata> stopVM(String id) {
        return facade.stopVirtualMachine(id);
    }

    @Override
    public Result<VirtualMachineMetadata> deleteVM(String id) {
        return facade.deleteVirtualMachine(id);
    }

}
