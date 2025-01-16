package com.project_nebula.compute_orchestrator.virtual_machine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metadata {

    private UUID id;
    private String region;

}
