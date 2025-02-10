package com.project_nebula.shared.compute;

import lombok.*;

@Builder
@AllArgsConstructor
@Data
public class ComputeNodeSpecs {
    private int cpus;
    private int memory;
    private int storage;
}
