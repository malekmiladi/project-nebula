package com.project_nebula.compute_orchestrator.compute.registration;

import com.project_nebula.compute_orchestrator.compute.ComputeService;
import com.project_nebula.grpc_common.orchestrator_registration.proto.OrchestratorRegistrationGrpc;
import com.project_nebula.grpc_common.orchestrator_registration.proto.RegistrationAcknowledge;
import com.project_nebula.grpc_common.orchestrator_registration.proto.RegistrationParameters;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComputeRegistrationService extends OrchestratorRegistrationGrpc.OrchestratorRegistrationImplBase {

    private final ComputeService computeService;

    @Override
    public void registerComputeNode(RegistrationParameters params, StreamObserver<RegistrationAcknowledge> responseObserver) {
        UUID id = computeService.register(params);
        RegistrationAcknowledge response = RegistrationAcknowledge.newBuilder()
                .setAck(true)
                .setId(id.toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
