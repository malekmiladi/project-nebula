package com.project_nebula.grpc_common;

import com.project_nebula.grpc_common.heartbeat.proto.HeartbeatServiceGrpc;
import com.project_nebula.grpc_common.orchestrator_registration.proto.OrchestratorRegistrationGrpc;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.VirtualMachineOperationsGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractBlockingStub;

public class BlockingStubFactory {

    private static ManagedChannel createChannel(GRPCClientConfiguration configuration) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(configuration.getHostname(), configuration.getPort());

        if (!configuration.isTlsEnable()) {
            channelBuilder.usePlaintext();
        } else {
            channelBuilder.useTransportSecurity();
        }

        return channelBuilder.build();
    }

    private static AbstractBlockingStub<?> configureStub(GRPCClientConfiguration configuration, AbstractBlockingStub<?> stub) {
        if (configuration.isTlsEnable()) {
            stub = stub.withCallCredentials(configuration.getCredentials());
        }
        // ... if there's any further config (compression, deadline...)

        return stub;
    }

    public static <T> AbstractBlockingStub<?> createStub(Class<T> grpcClass, GRPCClientConfiguration configuration) {
        ManagedChannel channel = createChannel(configuration);
        if (grpcClass == VirtualMachineOperationsGrpc.class) {
            return configureStub(configuration, VirtualMachineOperationsGrpc.newBlockingStub(channel));
        } else if (grpcClass == HeartbeatServiceGrpc.class) {
            return configureStub(configuration, HeartbeatServiceGrpc.newBlockingStub(channel));
        } else if (grpcClass == OrchestratorRegistrationGrpc.class) {
            return configureStub(configuration, OrchestratorRegistrationGrpc.newBlockingStub(channel));
        }
        return null;
    }

}
