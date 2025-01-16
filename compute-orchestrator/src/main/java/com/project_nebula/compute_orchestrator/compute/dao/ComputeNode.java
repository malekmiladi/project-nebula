package com.project_nebula.compute_orchestrator.compute.dao;

import com.project_nebula.compute_orchestrator.virtual_machine.dao.VirtualMachine;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "compute_node")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String state;
    private String hostname;
    private int port;

    @OneToMany(mappedBy = "node")
    private List<VirtualMachine> computeNodes;

}
