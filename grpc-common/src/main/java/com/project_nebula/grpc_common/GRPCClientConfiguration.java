package com.project_nebula.grpc_common;

import io.grpc.CallCredentials;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GRPCClientConfiguration {
    private String hostname;
    private int port;
    private boolean tlsEnable;
    private CallCredentials credentials;
}
