package project_nebula.compute_manager.project.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import project_nebula.compute_manager.virtual_machine.dao.VirtualMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "project")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private List<ProjectTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<VirtualMachine> instances = new ArrayList<>();

}
