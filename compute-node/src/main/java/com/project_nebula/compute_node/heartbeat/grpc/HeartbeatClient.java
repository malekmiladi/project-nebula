package com.project_nebula.compute_node.heartbeat.grpc;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.grpc.heartbeat.proto.*;
import com.project_nebula.compute_node.heartbeat.ComputeNodeStatus;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.time.Instant;

public class HeartbeatClient {

    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub blockingStub;
    private final CallCredentials callCredentials;
    private final ComputeConfiguration conf;

    public HeartbeatClient(ComputeConfiguration conf, CallCredentials callCredentials) {
        this.conf = conf;
        this.callCredentials = callCredentials;
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(conf.getGrpcServerHostname(), conf.getGrpcServerPort());
        if (!conf.isGrpcServerTLSEnable()) {
            channelBuilder.usePlaintext();
        }
        ManagedChannel channel = channelBuilder.build();
        this.blockingStub = HeartbeatServiceGrpc.newBlockingStub(channel);
    }

    private Heartbeat createNewHeartbeat() {
        Status status = Status.newBuilder()
                .setCode(ComputeNodeStatus.OK.ordinal())
                .setMessage("Ping")
                .build();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .setNanos(Instant.now().getNano())
                .build();
        return Heartbeat.newBuilder()
                .setId(conf.getId())
                .setStatus(status)
                .setTimestamp(timestamp)
                .build();
    }

    public boolean sendHeartBeat() {
        Heartbeat heartbeat = createNewHeartbeat();
        HeartbeatAcknowledge heartbeatAcknowledge;
        if (conf.isGrpcServerTLSEnable()) {
            heartbeatAcknowledge = blockingStub.withCallCredentials(callCredentials).sendHeartbeat(heartbeat);
        } else {
            heartbeatAcknowledge = blockingStub.sendHeartbeat(heartbeat);
        }
        return heartbeatAcknowledge.getAck();
    }

}
