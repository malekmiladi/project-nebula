package com.project_nebula.compute_node.resource.hypervisor;

public interface HypervisorInterface {

    void createVM(String id, VirtualMachineSpecs specs);
    void deleteVM(String id);

    void startVM(String id);
    void stopVM(String id);
    void restartVM(String id);

}
