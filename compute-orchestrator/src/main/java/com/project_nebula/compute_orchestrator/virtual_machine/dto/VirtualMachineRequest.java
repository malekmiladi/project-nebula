package com.project_nebula.compute_orchestrator.virtual_machine.dto;

import com.project_nebula.shared.resource.VirtualMachineSpecs;
import com.project_nebula.shared.resource.image.ImageMetadata;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class VirtualMachineRequest {

    InstanceMetadata metadata;
    VirtualMachineSpecs specs;
    ImageMetadata image;

}
