package com.project_nebula.hypervisor.resource.KVM.exceptions;

public class DomainDeleteException extends RuntimeException {
    public DomainDeleteException() {}

    public DomainDeleteException(String message) {
        super(message);
    }

    public DomainDeleteException(Throwable cause) {
        super(cause);
    }

    public DomainDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
