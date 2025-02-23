package com.project_nebula.shared.resource;

import com.project_nebula.shared.resource.image.ImageMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachineRequest {

    VirtualMachineRequestMetadata metadata;
    VirtualMachineSpecs specs;
    ImageMetadata image;

}
