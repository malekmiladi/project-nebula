package com.project_nebula.shared.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
