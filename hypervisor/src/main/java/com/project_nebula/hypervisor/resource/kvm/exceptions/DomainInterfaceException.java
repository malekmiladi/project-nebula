package com.project_nebula.hypervisor.resource.kvm.exceptions;

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
