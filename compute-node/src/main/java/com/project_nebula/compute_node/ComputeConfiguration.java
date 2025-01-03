package com.project_nebula.compute_node;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComputeConfiguration {

    public static String HYPER_VISOR_CONNECTION_URI;
    public static String CLOUD_DATASOURCE_URL;
    public static String STORAGE_POOL_NAME;

    @Value("${project-nebula.compute-node.hypervisor.kvm.connection.uri}")
    public void setHypervisorConnectionURI(final String hypervisorConnectionURI) {
        HYPER_VISOR_CONNECTION_URI = hypervisorConnectionURI;
    }

    @Value("${project-nebula.compute-node.cloud-datasource.url}")
    public void setCloudDataSourceURL(final String cloudDataSourceURL) {
        CLOUD_DATASOURCE_URL = cloudDataSourceURL;
    }

    @Value("${project-nebula.compute-node.hypervisor.storage-pool.default}")
    public void setStoragePoolName(final String storagePoolName) {
        STORAGE_POOL_NAME = storagePoolName;
    }

}
