package project_nebula.compute_manager.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Builder.Default
    private List<ProjectTagMetadata> tags = new ArrayList<>();

}
