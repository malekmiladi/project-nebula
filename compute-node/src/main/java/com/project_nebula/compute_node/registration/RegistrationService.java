package com.project_nebula.compute_node.registration;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.grpc_common.orchestrator_registration.proto.RegistrationAcknowledge;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.registration.RegistrationClient;
import com.project_nebula.shared.compute.ComputeNodeMetadata;
import com.project_nebula.shared.compute.ComputeNodeObject;
import com.project_nebula.shared.compute.ComputeNodeSpecs;
import com.project_nebula.shared.compute.ComputeNodeState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class RegistrationService implements CommandLineRunner {

    private final RegistrationClient registrationClient;
    private final GRPCClientConfiguration grpcClientConfiguration;
    private final ComputeNodeObject node;
    private final ComputeConfiguration conf;

    public RegistrationService(ComputeConfiguration conf) throws UnknownHostException {
        this.conf = conf;
        this.grpcClientConfiguration = GRPCClientConfiguration.builder()
                .hostname(conf.getOrchestratorHostname())
                .port(conf.getOrchestratorPort())
                .tlsEnable(conf.isOrchestratorTLSEnable())
                .build();
        ComputeNodeMetadata metadata = ComputeNodeMetadata.builder()
                .id(conf.getId().isEmpty() ? null : UUID.fromString(conf.getId()))
                .region(conf.getRegion())
                .state(ComputeNodeState.ACTIVE)
                .hostname(Inet4Address.getLocalHost().getHostAddress())
                .port(9090)
                .build();
        ComputeNodeSpecs specs = ComputeNodeSpecs.builder()
                .storage(conf.getStorage())
                .cpus(conf.getCpus())
                .memory(conf.getMemory())
                .build();
        this.node = ComputeNodeObject.builder()
                .metadata(metadata)
                .specs(specs)
                .build();
        this.registrationClient = new RegistrationClient(this.grpcClientConfiguration, this.node, null);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(node.toString());
        log.info("Virtual Machine Cloud Data Source: {}", conf.getCloudDatasourceUri());
        log.info("gRPC TLS enable: {}", grpcClientConfiguration.isTlsEnable());
        registerServer();
    }

    private void registerServer() throws Exception {
        boolean registered = false;
        while (!registered) {
            log.info("Registering compute node at '{}'", grpcClientConfiguration.getHostname() + ":" + grpcClientConfiguration.getPort());
            RegistrationAcknowledge registrationResponse = registrationClient.registerComputeNode();
            if (registrationResponse.getAck()) {
                conf.setNewId(registrationResponse.getId());
                registered = true;
                log.info("Registration success. Id: {}", registrationResponse.getId());
            } else {
                log.warn("Registration failed. Retrying in 30s...");
                Thread.sleep(Duration.ofSeconds(30));
            }
        }
    }

}
