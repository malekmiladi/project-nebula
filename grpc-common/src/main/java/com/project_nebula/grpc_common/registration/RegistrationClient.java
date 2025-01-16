package com.project_nebula.grpc_common.registration;

import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.compute_node.grpc_common.orchestrator_registration.proto.OrchestratorRegistrationGrpc;
import com.project_nebula.compute_node.grpc_common.orchestrator_registration.proto.RegistrationAcknowledge;
import com.project_nebula.compute_node.grpc_common.orchestrator_registration.proto.RegistrationParameters;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RegistrationClient {
    private final OrchestratorRegistrationGrpc.OrchestratorRegistrationBlockingStub blockingStub;
    private final CallCredentials callCredentials;
    private final GRPCClientConfiguration conf;
    private final ComputeNodeMetadata metadata;

    public RegistrationClient(GRPCClientConfiguration conf, ComputeNodeMetadata metadata, CallCredentials callCredentials) {
        this.conf = conf;
        this.metadata = metadata;
        this.callCredentials = callCredentials;
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(conf.getHostname(), conf.getPort());
        if (!conf.isTlsEnable()) {
            channelBuilder.usePlaintext();
        }
        ManagedChannel channel = channelBuilder.build();
        this.blockingStub = OrchestratorRegistrationGrpc.newBlockingStub(channel);
    }

    private RegistrationParameters createNewRegistrationRequest() {
        return RegistrationParameters.newBuilder()
                .setId(conf.getId())
                .setRegion(metadata.getRegion())
                .setSpareCpus(metadata.getCpus())
                .setSpareMemory(metadata.getMemory())
                .setSpareStorage(metadata.getStorage())
                .build();
    }

    public RegistrationAcknowledge registerComputeNode() {
        RegistrationParameters registrationParameters = createNewRegistrationRequest();
        RegistrationAcknowledge registrationAcknowledge;
        if (conf.isTlsEnable()) {
            return blockingStub.withCallCredentials(callCredentials).registerComputeNode(registrationParameters);
        } else {
            return blockingStub.registerComputeNode(registrationParameters);
        }
    }
}
