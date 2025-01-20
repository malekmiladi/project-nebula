package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachineRequestMetadata {
    private String region;
    private UUID id;
    private String authToken;
    private VirtualMachineState state;
}
