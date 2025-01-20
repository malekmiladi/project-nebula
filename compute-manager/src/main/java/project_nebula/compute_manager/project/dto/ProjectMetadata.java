package project_nebula.compute_manager.project.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMetadata {

    private UUID userId;
    private String name;
    private String description;
    private List<ProjectTagMetadata> tags = new ArrayList<>();

}
