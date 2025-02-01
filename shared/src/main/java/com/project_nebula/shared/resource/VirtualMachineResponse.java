package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineResponse {
    private UUID id;
    private VirtualMachineMetadata metadata;
    private VirtualMachineError error;
}
