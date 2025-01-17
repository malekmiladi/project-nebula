package com.project_nebula.shared.resource;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Data
public class VirtualMachineError {
    private final String message;
    private final VirtualMachineErrorType type;
}
