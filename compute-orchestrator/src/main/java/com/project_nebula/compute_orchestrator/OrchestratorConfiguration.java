package com.project_nebula.compute_orchestrator;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class OrchestratorConfiguration {

    private int heartbeatRate;

    @Value("${project-nebula.compute-orchestrator.compute-node.heartbeat.rate.seconds}")
    public void setHeartbeatRate(int threshold) {
        this.heartbeatRate = threshold;
    }

}
