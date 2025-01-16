package com.project_nebula.hypervisor.resource.kvm;

import org.libvirt.Connect;

public class NetworkManager {
    private final Connect hypervisorConn;
    public NetworkManager(Connect hypervisorConn) {
        this.hypervisorConn = hypervisorConn;
    }
}
