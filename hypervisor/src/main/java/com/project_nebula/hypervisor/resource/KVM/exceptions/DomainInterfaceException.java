package com.project_nebula.hypervisor.resource.KVM.exceptions;

import org.libvirt.LibvirtException;

public class DomainInterfaceException extends RuntimeException {
    public DomainInterfaceException() {}

    public DomainInterfaceException(String message) {
        super(message);
    }

    public DomainInterfaceException(Throwable cause) {
        super(cause);
    }

    public DomainInterfaceException(String message, Throwable cause) {
        super(message, cause);
    }
}
