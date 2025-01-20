package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class VirtualMachineConfigurationResponse {

    private final String authToken;
    private final UUID id;

}
