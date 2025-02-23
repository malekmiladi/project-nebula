package com.project_nebula.shared.compute;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ComputeNodeObject {
    private ComputeNodeMetadata metadata;
    private ComputeNodeSpecs specs;
}
