package com.project_nebula.hypervisor.resource.KVM.exceptions;

public class DomainStartException extends RuntimeException {
    public DomainStartException() {}

    public DomainStartException(String message) {
        super(message);
    }

    public DomainStartException(Throwable cause) {
        super(cause);
    }

    public DomainStartException(String message, Throwable cause) {
        super(message, cause);
    }
}
