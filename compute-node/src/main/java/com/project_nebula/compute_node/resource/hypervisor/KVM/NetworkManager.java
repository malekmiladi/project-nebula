package com.project_nebula.compute_node.resource.hypervisor.KVM;

import org.libvirt.Connect;

public class NetworkManager {
    private final Connect hypervisorConn;
    public NetworkManager(Connect hypervisorConn) {
        this.hypervisorConn = hypervisorConn;
    }
}
