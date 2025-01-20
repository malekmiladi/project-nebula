package com.project_nebula.shared.compute;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class ComputeNodeMetadata {
    private final UUID id;
    private final String region;
    private final String hostname;
    private final int port;
    private final ComputeNodeState state;
}
