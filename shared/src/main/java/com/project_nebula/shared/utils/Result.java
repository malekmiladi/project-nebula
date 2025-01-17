package com.project_nebula.shared.utils;

import com.project_nebula.shared.resource.VirtualMachineError;
import lombok.Getter;

@Getter
public class Result<T> {

    private final T value;
    private final VirtualMachineError error;

    private Result(T value, VirtualMachineError error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(VirtualMachineError error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

}
