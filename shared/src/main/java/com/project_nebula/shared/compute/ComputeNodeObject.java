package com.project_nebula.shared.compute;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class ComputeNodeObject {
    private final ComputeNodeMetadata metadata;
    private final ComputeNodeSpecs specs;
}
