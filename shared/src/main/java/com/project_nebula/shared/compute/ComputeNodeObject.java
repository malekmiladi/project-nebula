package com.project_nebula.shared.compute;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class ComputeNodeObject {
    private ComputeNodeMetadata metadata;
    private ComputeNodeSpecs specs;
}
