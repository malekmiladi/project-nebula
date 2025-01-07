package com.project_nebula.hypervisor;

import com.project_nebula.hypervisor.resource.VirtualMachineMetadata;
import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;
import com.project_nebula.hypervisor.resource.image.ImageMetadata;
import com.project_nebula.hypervisor.utils.Result;

public interface HypervisorInterface {

    Result<VirtualMachineMetadata> createVM(String id, VirtualMachineSpecs specs, ImageMetadata image, String cloudDataSourceUrl);
    Result<VirtualMachineMetadata> deleteVM(String id);

    Result<VirtualMachineMetadata> startVM(String id);
    Result<VirtualMachineMetadata> stopVM(String id);
    Result<VirtualMachineMetadata> restartVM(String id);

}
