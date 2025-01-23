package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VirtualMachineConfigurationResponse {

    private String authToken;
    private UUID id;

}
