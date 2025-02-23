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
public class VirtualMachineConfigurationRequest {

    private UUID id;
    private String config;

}
