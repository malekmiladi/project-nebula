package com.project_nebula.compute_orchestrator.heartbeat;

import com.project_nebula.grpc_common.heartbeat.proto.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase {

    @Override
    public void sendHeartbeat(Heartbeat heartbeat, StreamObserver<HeartbeatAcknowledge> responseObserver) {
        String id = heartbeat.getId();
        Timestamp timestamp = heartbeat.getTimestamp();
        Status status = heartbeat.getStatus();
    }

}
