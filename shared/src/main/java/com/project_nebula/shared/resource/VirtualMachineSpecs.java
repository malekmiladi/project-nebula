package com.project_nebula.shared.resource;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VirtualMachineSpecs {
    private int cpus;
    private int memory;
    private int disk;
}
