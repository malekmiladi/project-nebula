package com.project_nebula.compute_orchestrator.virtual_machine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specs {

    private int memory;
    private int disk;
    private int cpus;

}
