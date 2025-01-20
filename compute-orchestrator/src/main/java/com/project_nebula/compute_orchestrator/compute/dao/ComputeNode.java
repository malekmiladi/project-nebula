package com.project_nebula.compute_orchestrator.compute.dao;

import com.project_nebula.compute_orchestrator.virtual_machine.dao.VirtualMachineInstance;
import com.project_nebula.shared.compute.ComputeNodeState;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "compute_node")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ComputeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int defaultCpus;
    private int defaultMemory;
    private int defaultStorage;

    private int cpus;
    private int memory;
    private int storage;

    private String region;
    private ComputeNodeState state;
    private String hostname;
    private int port;

    private Timestamp heartbeatTimestamp;

    @OneToMany(mappedBy = "node")
    private List<VirtualMachineInstance> computeNodes;

}
