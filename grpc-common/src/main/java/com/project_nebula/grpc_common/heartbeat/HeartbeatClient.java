package com.project_nebula.grpc_common.heartbeat;

import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.heartbeat.proto.*;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HeartbeatClient {
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub blockingStub;
    private final CallCredentials callCredentials;
    private final GRPCClientConfiguration conf;

    public HeartbeatClient(GRPCClientConfiguration conf, CallCredentials callCredentials) {
        this.conf = conf;
        this.callCredentials = callCredentials;
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(conf.getHostname(), conf.getPort());
        if (!conf.isTlsEnable()) {
            channelBuilder.usePlaintext();
        }
        ManagedChannel channel = channelBuilder.build();
        this.blockingStub = HeartbeatServiceGrpc.newBlockingStub(channel);
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
        Heartbeat heartbeat = createNewHeartbeat(id);
        if (conf.isTlsEnable()) {
            return blockingStub
                    .withCallCredentials(callCredentials)
                    .sendHeartbeat(heartbeat)
                    .getAck();
        } else {
            return blockingStub
                    .sendHeartbeat(heartbeat)
                    .getAck();
        }
    }
}
