package com.project_nebula.compute_orchestrator.virtual_machine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachineRequest {

    private Specs specs;
    private Metadata metadata;

}
