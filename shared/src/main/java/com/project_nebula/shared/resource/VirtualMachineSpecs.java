package com.project_nebula.shared.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VirtualMachineSpecs {
    private int cpus;
    private int memory;
    private int disk;
}
