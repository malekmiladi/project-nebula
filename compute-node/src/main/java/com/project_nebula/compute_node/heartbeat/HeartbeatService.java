package com.project_nebula.compute_node.heartbeat;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.heartbeat.HeartbeatClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HeartbeatService {

    private final HeartbeatClient heartbeatClient;
    private final GRPCClientConfiguration clientConf;
    private final ComputeConfiguration computeConf;
    private boolean serverPong = false;

    public HeartbeatService(ComputeConfiguration conf) {
        this.computeConf = conf;
        this.clientConf = GRPCClientConfiguration.builder()
                .hostname(conf.getOrchestratorHostname())
                .port(conf.getOrchestratorPort())
                .tlsEnable(conf.isOrchestratorTLSEnable())
                .build();
        heartbeatClient = new HeartbeatClient(this.clientConf, null);
    }

    @Scheduled(fixedDelayString = "${project-nebula.compute-node.heartbeat.rate.seconds}", timeUnit = TimeUnit.SECONDS)
    public void heartbeat() {
        if (!computeConf.getId().isEmpty()) {
            boolean pong = heartbeatClient.sendHeartBeat(computeConf.getId());
            if (pong != serverPong) {
                if (pong) {
                    log.info("Server {} acknowledged heartbeat.", clientConf.getHostname());
                } else {
                    log.info("Server {} didn't respond to heartbeat.", clientConf.getHostname());
                }
            }
            serverPong = pong;
        }
    }

}
