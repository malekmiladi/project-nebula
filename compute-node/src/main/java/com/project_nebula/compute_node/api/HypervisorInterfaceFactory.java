package com.project_nebula.compute_node.api;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.hypervisor.HypervisorInterface;

public class HypervisorInterfaceFactory {

    private final ComputeConfiguration conf;

    public HypervisorInterfaceFactory(ComputeConfiguration conf) {
        this.conf = conf;
    }

    public HypervisorInterface getHypervisorInterface() {
        return switch (conf.getHypervisorType()) {
            // Might add more in the future
            case "KVM" -> new KVMInterface(conf);
            default -> null;
        };
    }
}
