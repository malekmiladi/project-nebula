package com.project_nebula.hypervisor.resource;

import com.project_nebula.hypervisor.resource.image.ImageInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class VirtualMachineSpecs {
    private final int vCpus;
    private final int vRamGb;
    private final int vDiskGb;
    private final ImageInfo image;
}
