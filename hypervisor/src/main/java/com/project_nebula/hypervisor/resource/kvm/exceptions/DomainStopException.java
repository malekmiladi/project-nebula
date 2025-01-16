package com.project_nebula.hypervisor.resource.kvm.exceptions;

public class DomainStopException extends RuntimeException {

    public DomainStopException() {}

    public DomainStopException(String message) {
      super(message);
    }

    public DomainStopException(Throwable cause) {
        super(cause);
    }

    public DomainStopException(String message, Throwable cause) {
      super(message, cause);
    }

}
