package com.project_nebula.hypervisor;

import com.project_nebula.hypervisor.utils.Result;
import com.project_nebula.shared.resource.VirtualMachineMetadata;
import com.project_nebula.shared.resource.VirtualMachineSpecs;
import com.project_nebula.shared.resource.image.ImageMetadata;

public interface HypervisorInterface {

    Result<VirtualMachineMetadata> createVM(String id, VirtualMachineSpecs specs, ImageMetadata image, String cloudDataSourceUrl);
    Result<VirtualMachineMetadata> deleteVM(String id);

    Result<VirtualMachineMetadata> startVM(String id);
    Result<VirtualMachineMetadata> stopVM(String id);
    Result<VirtualMachineMetadata> restartVM(String id);

}
