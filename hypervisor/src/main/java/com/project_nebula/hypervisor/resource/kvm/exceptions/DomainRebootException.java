package com.project_nebula.hypervisor.resource.kvm.exceptions;

public class DomainRebootException extends RuntimeException {
  public DomainRebootException() {}

  public DomainRebootException(String message) {
    super(message);
  }

  public DomainRebootException(Throwable cause) {
    super(cause);
  }

  public DomainRebootException(String message, Throwable cause) {
    super(message, cause);
  }
}
