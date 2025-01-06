package com.project_nebula.compute_node.api;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.utils.ApplicationControl;
import com.project_nebula.hypervisor.HypervisorInterface;
import com.project_nebula.hypervisor.resource.KVM.KVMFacade;
import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


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
    public void createVM(String id, VirtualMachineSpecs specs) {

    }

    @Override
    public void startVM(String id) {

    }

    @Override
    public void restartVM(String id) {

    }

    @Override
    public void stopVM(String id) {

    }

    @Override
    public void deleteVM(String id) {

    }

}
