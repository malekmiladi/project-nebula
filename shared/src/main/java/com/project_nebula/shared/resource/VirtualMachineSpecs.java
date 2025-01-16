package com.project_nebula.shared.resource;

import lombok.*;

@RequiredArgsConstructor
@Builder
@Getter
@Setter
@Data
public class VirtualMachineSpecs {
    private final int vCpus;
    private final int vRamGb;
    private final int vDiskGb;
}
