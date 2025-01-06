package com.project_nebula.compute_node.heartbeat;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.grpc.heartbeat.proto.HeartbeatServiceGrpc;
import com.project_nebula.compute_node.heartbeat.grpc.HeartbeatClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase {

    private final HeartbeatClient heartbeatClient;
    private final ComputeConfiguration conf;
    private boolean serverPong = false;

    public HeartbeatService(ComputeConfiguration conf) {
        this.conf = conf;
        heartbeatClient = new HeartbeatClient(conf, null);
    }

    //@Scheduled(fixedRate = 5000)
    public void heartbeat() {
        boolean pong = heartbeatClient.sendHeartBeat();
        if (pong != serverPong) {
            if (pong) {
                log.info("Server {} acknowledged heartbeat.", conf.getGrpcServerHostname());
            } else {
                log.info("Server {} didn't respond to heartbeat.", conf.getGrpcServerHostname());
            }
        }
        serverPong = pong;
    }

}
