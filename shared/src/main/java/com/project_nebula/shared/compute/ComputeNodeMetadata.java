package com.project_nebula.shared.compute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class ComputeNodeMetadata {
    private UUID id;
    private String region;
    private String hostname;
    private int port;
    private ComputeNodeState state;
}
