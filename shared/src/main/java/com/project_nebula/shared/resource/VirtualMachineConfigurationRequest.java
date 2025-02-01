package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineConfigurationRequest {

    private UUID id;
    private String config;

}
