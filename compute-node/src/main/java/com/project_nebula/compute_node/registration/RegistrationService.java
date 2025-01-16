package com.project_nebula.compute_node.registration;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.grpc_common.orchestrator_registration.proto.RegistrationAcknowledge;
import com.project_nebula.grpc_common.registration.ComputeNodeMetadata;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.registration.RegistrationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class RegistrationService implements CommandLineRunner {

    private final RegistrationClient registrationClient;
    private final GRPCClientConfiguration grpcClientConfiguration;
    private final ComputeNodeMetadata metadata;
    private final ComputeConfiguration conf;

    public RegistrationService(ComputeConfiguration conf) {
        this.conf = conf;
        this.grpcClientConfiguration = GRPCClientConfiguration.builder()
                .id(conf.getId())
                .hostname(conf.getGrpcServerHostname())
                .port(conf.getGrpcServerPort())
                .tlsEnable(conf.isGrpcServerTLSEnable())
                .build();
        this.metadata = ComputeNodeMetadata.builder()
                .cpus(conf.getSpareCpus())
                .memory(conf.getSpareMemory())
                .region(conf.getRegion())
                .storage(conf.getSpareStorage())
                .cloudDatasourceUrl(conf.getCloudDatasourceUri())
                .build();
        this.registrationClient = new RegistrationClient(this.grpcClientConfiguration, this.metadata, null);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Registering compute node at '{}'", grpcClientConfiguration.getHostname() + ":" + grpcClientConfiguration.getPort());
        log.info("Compute node specs: [{} vCPUS] [{}GB vRAM] [{}GB vDISK]", metadata.getCpus(), metadata.getMemory(), metadata.getStorage());
        log.info("Virtual Machine Cloud Data Source: {}", metadata.getCloudDatasourceUrl());
        log.info("gRPC TLS enable: {}", grpcClientConfiguration.isTlsEnable());
        //registerServer();
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
