package project_nebula.compute_manager.project;

import project_nebula.compute_manager.project.dao.ProjectTag;
import project_nebula.compute_manager.project.dto.ProjectTagMetadata;

public class ProjectTagMapper {
    public static ProjectTag toProjectTag(ProjectTagMetadata tag) {
        return ProjectTag.builder()
                .value(tag.getValue())
                .id(tag.getId())
                .build();
    }

    public static ProjectTagMetadata toProjectTagMetadata(ProjectTag tag) {
        return ProjectTagMetadata.builder()
                .id(tag.getId())
                .value(tag.getValue())
                .build();
    }

}
