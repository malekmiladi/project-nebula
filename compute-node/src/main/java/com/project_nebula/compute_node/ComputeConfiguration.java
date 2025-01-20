package com.project_nebula.compute_node;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Getter
@Component
@Slf4j
public class ComputeConfiguration {

    private int heartbeatRate;
    private String id;
    private String idFilePath;
    private String hypervisor;
    private String hypervisorConnectionURI;
    private String networkName;
    private String cloudDatasourceUri;
    private String storagePoolName;
    private String storagePoolLocation;
    private String orchestratorHostname;
    private int orchestratorPort;
    private boolean orchestratorTLSEnable;
    private int cpus;
    private int memory;
    private int storage;
    private String region;

    @Value("${project-nebula.compute-node.service.id}")
    private void setId(String id) {
        this.id = id;
    }

    @Value("${project-nebula.compute-node.service.id.file}")
    private void setIdFilePath(String idFilePath) {
        this.idFilePath = idFilePath;
    }

    @Value("${project-nebula.compute-node.hypervisor}")
    private void setHypervisor(String hypervisor) {
        this.hypervisor = hypervisor;
    }

    @Value("${project-nebula.compute-node.hypervisor.kvm.connection.uri}")
    private void setHypervisorConnectionURI(String hypervisorConnectionURI) {
        this.hypervisorConnectionURI = hypervisorConnectionURI;
    }

    @Value("${project-nebula.compute-node.cloud-datasource.url}")
    private void setCloudDatasourceUri(String cloudDatasourceUri) {
        this.cloudDatasourceUri = cloudDatasourceUri;
    }

    @Value("${project-nebula.compute-node.hypervisor.storage-pool.default.name}")
    private void setStoragePoolName(String storagePoolName) {
        this.storagePoolName = storagePoolName;
    }

    @Value("${project-nebula.compute-node.hypervisor.network.name}")
    private void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    @Value("${project-nebula.compute-node.hypervisor.storage-pool.default.location}")
    private void setStoragePoolLocation(String storagePoolLocation) {
        this.storagePoolLocation = storagePoolLocation;
    }

    @Value("${project-nebula.compute-node.grpc.compute-orchestrator.server.host}")
    private void setOrchestratorHostname(String orchestratorHostname) {
        this.orchestratorHostname = orchestratorHostname;
    }

    @Value("${project-nebula.compute-node.grpc.orchestrator.server.port}")
    private void setOrchestratorPort(int orchestratorPort) {
        this.orchestratorPort = orchestratorPort;
    }

    @Value("${project-nebula.compute-node.grpc.orchestrator.server.tls}")
    private void setOrchestratorTLSEnable(boolean orchestratorTLSEnable) {
        this.orchestratorTLSEnable = orchestratorTLSEnable;
    }

    @Value("${project-nebula.compute-node.resource.cpu}")
    private void setCpus(int cpus) {
        this.cpus = cpus;
    }

    @Value("${project-nebula.compute-node.resource.memory}")
    private void setMemory(int memory) {
        this.memory = memory;
    }

    @Value("${project-nebula.compute-node.resource.storage}")
    private void setStorage(String storage) {
        this.storage = Integer.parseInt(storage);
    }

    @Value("${project-nebula.compute-node.service.region}")
    private void setRegion(String region) {
        this.region = region;
    }

    @Value("${project-nebula.compute-orchestrator.heartbeat.rate.seconds}")
    private void setHeartbeatRate(int heartbeatRate) {
        this.heartbeatRate = heartbeatRate;
    }

    public void setNewId(String id) throws IOException {
        this.id = id;
        File idFile = new File(idFilePath);
        FileWriter fw = new FileWriter(idFile.getAbsoluteFile());
        fw.write("project-nebula.compute-node.service.id=" + id + "\n");
        fw.flush();
        fw.close();
    }

}
