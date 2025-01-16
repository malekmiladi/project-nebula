package com.project_nebula.hypervisor.resource.KVM.exceptions;

public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException() {}

    public DomainNotFoundException(String message) {
        super(message);
    }

    public DomainNotFoundException(Throwable cause) {
        super(cause);
    }

    public DomainNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
