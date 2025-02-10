package com.project_nebula.grpc_common.registration;

import com.project_nebula.grpc_common.BlockingStubFactory;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.orchestrator_registration.proto.*;
import com.project_nebula.shared.compute.ComputeNodeObject;

public class RegistrationClient {

    private final OrchestratorRegistrationGrpc.OrchestratorRegistrationBlockingStub blockingStub;
    private final ComputeNodeObject node;

    public RegistrationClient(GRPCClientConfiguration conf, ComputeNodeObject node) {
        this.node = node;
        this.blockingStub = (OrchestratorRegistrationGrpc.OrchestratorRegistrationBlockingStub) BlockingStubFactory
                .createStub(OrchestratorRegistrationGrpc.class, conf);
    }

    private RegistrationParameters createNewRegistrationRequest() {
        ComputeMetadata metadata = ComputeMetadata.newBuilder()
                .setId(node.getMetadata().getId() == null ? "" : node.getMetadata().getId().toString())
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
        return blockingStub.registerComputeNode(createNewRegistrationRequest());
    }
}
