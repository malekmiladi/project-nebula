package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.compute_orchestrator.virtual_machine.dao.VirtualMachineInstance;
import com.project_nebula.shared.resource.VirtualMachineState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachineInstance, UUID> {
    @Modifying
    @Query(
        value = "UPDATE " +
                    "virtual_machine " +
                "SET state = :state " +
                "WHERE id = :id;",
        nativeQuery = true
    )
    VirtualMachineInstance updateStateById(@Param("id") UUID id, @Param("state") VirtualMachineState state);
    VirtualMachineInstance findOneByName(UUID name);
}
