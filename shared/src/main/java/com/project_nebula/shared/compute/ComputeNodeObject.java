package com.project_nebula.shared.compute;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class ComputeNodeObject {
    private final ComputeNodeMetadata metadata;
    private final ComputeNodeSpecs specs;
}
