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

    private String id;
    private String idFilePath;
    private String hypervisorType;
    private String hyperVisorConnectionURI;
    private String cloudDatasourceUri;
    private String storagePoolName;
    private String grpcServerHostname;
    private int grpcServerPort;
    private boolean grpcServerTLSEnable;
    private int spareCpus;
    private int spareMemory;
    private int spareStorage;
    private String region;

    @Value("${project-nebula.compute-node.service.id}")
    private void setId(String id) {
        this.id = id;
    }

    @Value("${project-nebula.compute-node.service.id.file}")
    private void setIdFilePath(String idFilePath) {
        this.idFilePath = idFilePath;
    }

    @Value("${project-nebula.compute-node.hypervisor.type}")
    private void setHypervisorType(String hypervisorType) {
        this.hypervisorType = hypervisorType;
    }

    @Value("${project-nebula.compute-node.hypervisor.kvm.connection.uri}")
    private void setHyperVisorConnectionURI(String hyperVisorConnectionURI) {
        this.hyperVisorConnectionURI = hyperVisorConnectionURI;
    }

    @Value("${project-nebula.compute-node.cloud-datasource.url}")
    private void setCloudDatasourceUri(String cloudDatasourceUri) {
        this.cloudDatasourceUri = cloudDatasourceUri;
    }

    @Value("${project-nebula.compute-node.hypervisor.storage-pool.default}")
    private void setStoragePoolName(String storagePoolName) {
        this.storagePoolName = storagePoolName;
    }

    @Value("${project-nebula.compute-node.grpc.server.host}")
    private void setGrpcServerHostname(String grpcServerHostname) {
        this.grpcServerHostname = grpcServerHostname;
    }

    @Value("${project-nebula.compute-node.grpc.server.port}")
    private void setGrpcServerPort(int grpcServerPort) {
        this.grpcServerPort = grpcServerPort;
    }

    @Value("${project-nebula.compute-node.grpc.server.tls}")
    private void setGrpcServerTLSEnable(boolean grpcServerTLSEnable) {
        this.grpcServerTLSEnable = grpcServerTLSEnable;
    }

    @Value("${project-nebula.compute-node.resource.cpu}")
    private void setSpareCpus(int spareCpus) {
        this.spareCpus = spareCpus;
    }

    @Value("${project-nebula.compute-node.resource.memory}")
    private void setSpareMemory(int spareMemory) {
        this.spareMemory = spareMemory;
    }

    @Value("${project-nebula.compute-node.resource.storage}")
    private void setSpareStorage(String spareStorage) {
        this.spareStorage = Integer.parseInt(spareStorage);
    }

    @Value("${project-nebula.compute-node.service.region}")
    private void setRegion(String region) {
        this.region = region;
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
