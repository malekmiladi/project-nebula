package project_nebula.compute_manager.project.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "project_tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
