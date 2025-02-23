package com.project_nebula.shared.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VirtualMachineConfigurationResponse {

    private String authToken;
    private UUID id;

}
