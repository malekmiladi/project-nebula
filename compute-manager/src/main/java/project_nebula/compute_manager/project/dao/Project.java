package project_nebula.compute_manager.project.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project_nebula.compute_manager.virtual_machine.dao.VirtualMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "project")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.MERGE)
    @Nonnull
    @Builder.Default
    private List<ProjectTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<VirtualMachine> instances = new ArrayList<>();

}
