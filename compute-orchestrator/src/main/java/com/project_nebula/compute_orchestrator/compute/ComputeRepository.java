package com.project_nebula.compute_orchestrator.compute;

import com.project_nebula.compute_orchestrator.compute.dao.ComputeNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComputeRepository extends JpaRepository<ComputeNode, UUID> {
}
