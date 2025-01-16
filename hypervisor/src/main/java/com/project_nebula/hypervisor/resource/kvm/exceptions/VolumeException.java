package com.project_nebula.hypervisor.resource.kvm.exceptions;

public class VolumeException extends RuntimeException {

    public VolumeException() {}

    public VolumeException(String message) {
        super(message);
    }

    public VolumeException(Throwable cause) {
        super(cause);
    }

    public VolumeException(String message, Throwable cause) {
        super(message, cause);
    }
}
