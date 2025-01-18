package com.project_nebula.shared.resource;

import lombok.*;

@RequiredArgsConstructor
@Builder
@Getter
@Setter
@Data
public class VirtualMachineSpecs {
    private final int cpus;
    private final int memory;
    private final int disk;
}
