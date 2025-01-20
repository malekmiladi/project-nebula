package project_nebula.compute_manager.project.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTagMetadata {

    private UUID id;
    private String value;

}
