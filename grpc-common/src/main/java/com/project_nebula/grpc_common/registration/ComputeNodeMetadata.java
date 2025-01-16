package com.project_nebula.grpc_common.registration;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ComputeNodeMetadata {
    private final String region;
    private final int cpus;
    private final int memory;
    private final int storage;
    private final String cloudDatasourceUrl;
}
