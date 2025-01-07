package com.project_nebula.compute_node.registration;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.grpc.orchestrator_registration.proto.RegistrationAcknowledge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class RegistrationService implements CommandLineRunner {

    private final RegistrationClient registrationClient;
    private final ComputeConfiguration conf;

    public RegistrationService(ComputeConfiguration conf) {
        this.conf = conf;
        this.registrationClient = new RegistrationClient(conf, null);
    }

    @Override
    public void run(String... args) throws Exception {
        registerServer();
    }

    private void registerServer() throws Exception {
        boolean registered = false;
        while (!registered) {
            RegistrationAcknowledge registrationResponse = registrationClient.registerComputeNode();
            if (registrationResponse.getAck()) {
                conf.setNewId(registrationResponse.getId());
                registered = true;
            } else {
                log.warn("Registration failed. Retrying in 30s...");
                Thread.sleep(Duration.ofSeconds(30));
            }
        }
    }

}
