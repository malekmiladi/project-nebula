package com.project_nebula.compute_orchestrator.virtual_machine.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class InstanceMetadata {
    private String region;
    private UUID id;
}
