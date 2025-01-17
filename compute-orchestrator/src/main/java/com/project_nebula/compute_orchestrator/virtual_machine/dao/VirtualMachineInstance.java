package com.project_nebula.compute_orchestrator.virtual_machine.dao;

import com.project_nebula.compute_orchestrator.compute.dao.ComputeNode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "virtual_machine")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VirtualMachineInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "compute_node_id")
    private ComputeNode node;

    private UUID name;

    @CreatedDate
    private Date createdAt;

}
