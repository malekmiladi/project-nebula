package com.project_nebula.grpc_common.registration;

import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.orchestrator_registration.proto.*;
import com.project_nebula.shared.compute.ComputeNodeObject;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RegistrationClient {

    private final OrchestratorRegistrationGrpc.OrchestratorRegistrationBlockingStub blockingStub;
    private final CallCredentials callCredentials;
    private final GRPCClientConfiguration conf;
    private final ComputeNodeObject node;

    public RegistrationClient(GRPCClientConfiguration conf, ComputeNodeObject node, CallCredentials callCredentials) {
        this.conf = conf;
        this.node = node;
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
        ComputeMetadata metadata = ComputeMetadata.newBuilder()
                .setId(node.getMetadata().getId().toString())
                .setRegion(node.getMetadata().getRegion())
                .setHostname(node.getMetadata().getHostname())
                .setPort(node.getMetadata().getPort())
                .setState(node.getMetadata().getState().name())
                .build();
        ComputeSpecs specs = ComputeSpecs.newBuilder()
                .setCpus(node.getSpecs().getCpus())
                .setMemory(node.getSpecs().getMemory())
                .setStorage(node.getSpecs().getStorage())
                .build();
        return RegistrationParameters.newBuilder()
                .setMetadata(metadata)
                .setSpecs(specs)
                .build();
    }

    public RegistrationAcknowledge registerComputeNode() {
        RegistrationParameters registrationParameters = createNewRegistrationRequest();
        if (conf.isTlsEnable()) {
            return blockingStub.withCallCredentials(callCredentials).registerComputeNode(registrationParameters);
        } else {
            return blockingStub.registerComputeNode(registrationParameters);
        }
    }
}
