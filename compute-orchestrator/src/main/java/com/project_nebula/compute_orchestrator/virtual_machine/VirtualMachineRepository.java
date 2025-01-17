package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.compute_orchestrator.virtual_machine.dao.VirtualMachineInstance;
import com.project_nebula.shared.resource.VirtualMachineState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachineInstance, UUID> {
    VirtualMachineInstance updateStateById(UUID id, VirtualMachineState state);
    VirtualMachineInstance findOneByName(UUID name);
}
