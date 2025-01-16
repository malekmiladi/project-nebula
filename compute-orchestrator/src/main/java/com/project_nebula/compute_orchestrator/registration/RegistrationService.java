package com.project_nebula.compute_orchestrator.registration;

import com.project_nebula.grpc_common.orchestrator_registration.proto.OrchestratorRegistrationGrpc;
import com.project_nebula.grpc_common.orchestrator_registration.proto.RegistrationAcknowledge;
import com.project_nebula.grpc_common.orchestrator_registration.proto.RegistrationParameters;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class RegistrationService extends OrchestratorRegistrationGrpc.OrchestratorRegistrationImplBase {
    @Override
    public void registerComputeNode(RegistrationParameters params, StreamObserver<RegistrationAcknowledge> responseObserver) {
        RegistrationAcknowledge response = RegistrationAcknowledge.newBuilder()
                .setAck(true)
                .setId(UUID.randomUUID().toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
