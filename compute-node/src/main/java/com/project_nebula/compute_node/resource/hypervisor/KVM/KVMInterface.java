package com.project_nebula.compute_node.resource.hypervisor.KVM;

import com.project_nebula.compute_node.resource.hypervisor.HypervisorInterface;
import com.project_nebula.compute_node.resource.hypervisor.VirtualMachineSpecs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KVMInterface implements HypervisorInterface {

    private final KVMFacade facade = new KVMFacade();

    @Override
    public void createVM(String id, VirtualMachineSpecs specs) {

    }

    @Override
    public void restartVM(String id) {

    }

    @Override
    public void deleteVM(String id) {

    }

    @Override
    public void startVM(String id) {

    }

    @Override
    public void stopVM(String id) {

    }
}
