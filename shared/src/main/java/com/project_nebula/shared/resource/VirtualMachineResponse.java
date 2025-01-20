package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class VirtualMachineResponse {
    private final UUID id;
    private final VirtualMachineMetadata metadata;
    private final VirtualMachineError error;
}
