package project_nebula.compute_manager.project;

import org.springframework.data.jpa.repository.JpaRepository;
import project_nebula.compute_manager.project.dao.Project;
import project_nebula.compute_manager.project.dto.ProjectData;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<ProjectData> findByUserId(UUID userId);
}
