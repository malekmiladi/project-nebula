package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class VirtualMachineConfigurationRequest {

    private final UUID id;
    private final String config;

}
