package com.project_nebula.compute_orchestrator.compute;

import com.project_nebula.compute_orchestrator.compute.dao.ComputeNode;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ComputeRepository extends JpaRepository<ComputeNode, UUID> {

    ComputeNode getComputeNodeById(UUID id);

    @Query(
        value = "SELECT " +
                    "id, " +
                    "cpus, " +
                    "memory, " +
                    "storage, " +
                    "region, " +
                    "state, " +
                    "hostname, " +
                    "port, " +
                    "default_cpus, " +
                    "default_memory, " +
                    "default_storage, " +
                    "heartbeat_timestamp," +
                    "CURRENT_TIMESTAMP " +
                "FROM compute_node WHERE " +
                        "EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - heartbeat_timestamp)) <= :heartbeatRate " +
                "LIMIT 1;",
        nativeQuery = true
    )
    ComputeNode findOneByVirtualMachineSpecsAndHeartbeatTimeThreshold(@Param("heartbeatRate") int heartbeatRate);
    boolean existsById(@Nonnull UUID id);
}
