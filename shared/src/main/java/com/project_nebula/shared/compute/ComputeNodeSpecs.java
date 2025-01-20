package com.project_nebula.shared.compute;

import lombok.*;

@Builder
@AllArgsConstructor
@Data
public class ComputeNodeSpecs {
    private final int cpus;
    private final int memory;
    private final int storage;
}
