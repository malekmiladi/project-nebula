package com.project_nebula.grpc_common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GRPCClientConfiguration {
    private String id;
    private String hostname;
    private int port;
    private boolean tlsEnable;
}
