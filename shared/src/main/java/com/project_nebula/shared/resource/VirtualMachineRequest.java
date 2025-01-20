package com.project_nebula.shared.resource;

import com.project_nebula.shared.resource.image.ImageMetadata;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachineRequest {

    VirtualMachineRequestMetadata metadata;
    VirtualMachineSpecs specs;
    ImageMetadata image;

}
