package com.project_nebula.hypervisor;

import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;

public interface HypervisorInterface {

    void createVM(String id, VirtualMachineSpecs specs);
    void deleteVM(String id);

    void startVM(String id);
    void stopVM(String id);
    void restartVM(String id);

}
