package com.project_nebula.hypervisor.resource.KVM.exceptions;

public class DomainCreateException extends RuntimeException {
    public DomainCreateException() {}

    public DomainCreateException(String message) {
        super(message);
    }

    public DomainCreateException(Throwable cause) {
        super(cause);
    }

    public DomainCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
