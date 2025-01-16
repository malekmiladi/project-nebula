package com.project_nebula.compute_node.heartbeat;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.heartbeat.HeartbeatClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HeartbeatService {

    private final HeartbeatClient heartbeatClient;
    private final GRPCClientConfiguration conf;
    private boolean serverPong = false;

    public HeartbeatService(ComputeConfiguration conf) {
        this.conf = GRPCClientConfiguration.builder()
                .id(conf.getId())
                .hostname(conf.getGrpcServerHostname())
                .port(conf.getGrpcServerPort())
                .tlsEnable(conf.isGrpcServerTLSEnable())
                .build();;
        heartbeatClient = new HeartbeatClient(this.conf, null);
    }

    @Scheduled(fixedRate = 5000)
    public void heartbeat() {
        boolean pong = heartbeatClient.sendHeartBeat();
        if (pong != serverPong) {
            if (pong) {
                log.info("Server {} acknowledged heartbeat.", conf.getHostname());
            } else {
                log.info("Server {} didn't respond to heartbeat.", conf.getHostname());
            }
        }
        serverPong = pong;
    }

}
