package com.project_nebula.compute_orchestrator.virtual_machine.dto;

import com.project_nebula.shared.resource.VirtualMachineError;
import com.project_nebula.shared.resource.VirtualMachineMetadata;
import lombok.*;

import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class VirtualMachineResponse {
    private final UUID id;
    private final VirtualMachineMetadata metadata;
    private final VirtualMachineError error;
}
