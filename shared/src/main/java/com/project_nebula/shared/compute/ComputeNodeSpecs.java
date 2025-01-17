package com.project_nebula.shared.compute;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@Data
public class ComputeNodeSpecs {
    private final int cpus;
    private final int memory;
    private final int storage;
}
