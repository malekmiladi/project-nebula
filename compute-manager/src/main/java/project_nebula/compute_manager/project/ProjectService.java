package project_nebula.compute_manager.project;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import project_nebula.compute_manager.project.dao.Project;
import project_nebula.compute_manager.project.dao.ProjectTag;
import project_nebula.compute_manager.project.dto.ProjectData;
import project_nebula.compute_manager.project.dto.ProjectTagMetadata;

import java.util.*;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectTagRepository projectTagRepository;

    public ProjectData createProject(ProjectData projectData) {
        Project newProject = ProjectMapper.toProject(projectData);
        Project savedProject = projectRepository.save(newProject);
        List<ProjectTag> tags = new ArrayList<>();
        for (ProjectTagMetadata tag : projectData.getMetadata().getTags()) {
            ProjectTag newTag = ProjectTagMapper.toProjectTag(tag);
            newTag.setProject(savedProject);
            tags.add(newTag);
        }
        projectTagRepository.saveAll(tags);
        return ProjectMapper.toProjectData(newProject);
    }

    public ProjectData updateProject(UUID id, ProjectData projectData) throws NoSuchElementException {
        if (projectRepository.findById(id).isPresent()) {
            Project project = ProjectMapper.toProject(projectData);
            Project savedProject = projectRepository.save(project);
            savedProject.setId(id);
            return ProjectMapper.toProjectData(savedProject);
        } else {
            throw new NoSuchElementException("Project with id " + id + " does not exist");
        }
    }

    public void deleteProject(UUID id) {
        if (projectRepository.findById(id).isPresent()) {
            projectRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Project with id " + id + " does not exist");
        }
    }

    public List<ProjectData> getAllProjects(UUID userId) {
        return projectRepository.findByUserId(userId);
    }

    public ProjectTagMetadata addTag(UUID projectId, ProjectTagMetadata tag) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            ProjectTag projectTag = ProjectTagMapper.toProjectTag(tag);
            projectTag.setProject(project.get());
            projectTagRepository.save(projectTag);
            return ProjectTagMapper.toProjectTagMetadata(projectTag);
        } else {
            throw new NoSuchElementException("Project with id " + projectId + " does not exist");
        }
    }

    public ProjectTagMetadata updateTag(UUID projectId, UUID tagId, ProjectTagMetadata tag) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<ProjectTag> optionalProjectTag = projectTagRepository.findById(tagId);
        if (optionalProject.isPresent() && optionalProjectTag.isPresent()) {
            Project project = optionalProject.get();
            ProjectTag projectTag = optionalProjectTag.get();
            if (projectTag.getProject().getId().equals(project.getId())) {
                ProjectTag updatedTag = projectTagRepository.save(ProjectTagMapper.toProjectTag(tag));
                return ProjectTagMapper.toProjectTagMetadata(updatedTag);
            }
        }
        throw new NoSuchElementException(
            "Either project with id " + projectId + " does not exist," +
            " or tag with id " + tagId + " does not exist," +
            " or project with id " + projectId + " does not have tag with id " + tagId
        );
    }

    public void deleteTag(UUID projectId, UUID tagId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<ProjectTag> optionalProjectTag = projectTagRepository.findById(tagId);
        if (optionalProject.isPresent() && optionalProjectTag.isPresent()) {
            Project project = optionalProject.get();
            ProjectTag projectTag = optionalProjectTag.get();
            if (projectTag.getProject().getId().equals(project.getId())) {
                projectTagRepository.delete(projectTag);
            }
        }
        throw new NoSuchElementException(
            "Either project with id " + projectId + " does not exist," +
            " or tag with id " + tagId + " does not exist," +
            " or project with id " + projectId + " does not have tag with id " + tagId
        );
    }

    public Optional<Project> getProjectById(UUID id) {
        return projectRepository.findById(id);
    }

}
