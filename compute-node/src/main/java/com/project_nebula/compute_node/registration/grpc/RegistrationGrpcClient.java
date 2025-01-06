package com.project_nebula.compute_node.registration.grpc;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.grpc.orchestrator_registration.proto.OrchestratorRegistrationGrpc;
import com.project_nebula.compute_node.grpc.orchestrator_registration.proto.RegistrationAcknowledge;
import com.project_nebula.compute_node.grpc.orchestrator_registration.proto.RegistrationParameters;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RegistrationGrpcClient {

    private final OrchestratorRegistrationGrpc.OrchestratorRegistrationBlockingStub blockingStub;
    private final CallCredentials callCredentials;
    private final ComputeConfiguration conf;

    public RegistrationGrpcClient(ComputeConfiguration conf, CallCredentials callCredentials) {
        this.conf = conf;
        this.callCredentials = callCredentials;
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(conf.getGrpcServerHostname(), conf.getGrpcServerPort());
        if (!conf.isGrpcServerTLSEnable()) {
            channelBuilder.usePlaintext();
        }
        ManagedChannel channel = channelBuilder.build();
        this.blockingStub = OrchestratorRegistrationGrpc.newBlockingStub(channel);
    }

    private RegistrationParameters createNewRegistrationRequest() {
        return RegistrationParameters.newBuilder()
                .setId(conf.getId())
                .setRegion(conf.getRegion())
                .setSpareCpus(conf.getSpareCpus())
                .setSpareMemory(conf.getSpareMemory())
                .setSpareStorage(conf.getSpareStorage())
                .build();
    }

    public RegistrationAcknowledge registerComputeNode() {
        RegistrationParameters registrationParameters = createNewRegistrationRequest();
        RegistrationAcknowledge registrationAcknowledge;
        if (conf.isGrpcServerTLSEnable()) {
            registrationAcknowledge = blockingStub.withCallCredentials(callCredentials).registerComputeNode(registrationParameters);
        } else {
            registrationAcknowledge = blockingStub.registerComputeNode(registrationParameters);
        }
        return registrationAcknowledge;
    }

}
