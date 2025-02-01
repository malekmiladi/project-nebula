package com.project_nebula.shared.resource;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VirtualMachineError {
    private String message;
    private VirtualMachineErrorType type;
}
