package project_nebula.compute_manager.project;

import org.springframework.data.jpa.repository.JpaRepository;
import project_nebula.compute_manager.project.dao.ProjectTag;

import java.util.UUID;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, UUID> {
}
