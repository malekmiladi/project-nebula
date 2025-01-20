package com.project_nebula.compute_orchestrator.virtual_machine.dao;

import com.project_nebula.compute_orchestrator.compute.dao.ComputeNode;
import com.project_nebula.shared.resource.VirtualMachineState;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "virtual_machine")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VirtualMachineInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "compute_node_id")
    private ComputeNode node;

    private UUID name;

    private VirtualMachineState state;

    @CreatedDate
    private Date createdAt;

}
