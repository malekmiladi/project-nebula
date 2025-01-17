package com.project_nebula.compute_orchestrator.compute.heartbeat;

import com.project_nebula.compute_orchestrator.compute.ComputeService;
import com.project_nebula.grpc_common.heartbeat.proto.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase {

    private final ComputeService computeService;

    @Override
    public void sendHeartbeat(Heartbeat heartbeat, StreamObserver<HeartbeatAcknowledge> responseObserver) {
        boolean ack = computeService.recordHeartbeat(heartbeat);
        HeartbeatAcknowledge response = HeartbeatAcknowledge.newBuilder()
                .setAck(ack)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
