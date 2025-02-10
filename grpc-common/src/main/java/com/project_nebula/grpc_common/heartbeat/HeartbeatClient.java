package com.project_nebula.grpc_common.heartbeat;

import com.project_nebula.grpc_common.BlockingStubFactory;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.heartbeat.proto.*;

public class HeartbeatClient {
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub blockingStub;

    public HeartbeatClient(GRPCClientConfiguration conf) {
        this.blockingStub = (HeartbeatServiceGrpc.HeartbeatServiceBlockingStub) BlockingStubFactory
                .createStub(HeartbeatServiceGrpc.class, conf);
    }

    private Heartbeat createNewHeartbeat(String id) {
        Status status = Status.newBuilder()
                .setCode(ClientStatus.OK.ordinal())
                .setMessage("Ping")
                .build();
        return Heartbeat.newBuilder()
                .setId(id)
                .setStatus(status)
                .build();
    }

    public boolean sendHeartBeat(String id) {
        return blockingStub
                .sendHeartbeat(createNewHeartbeat(id))
                .getAck();
    }
}
