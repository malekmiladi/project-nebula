package com.project_nebula.shared.compute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ComputeNodeSpecs {
    private int cpus;
    private int memory;
    private int storage;
}
