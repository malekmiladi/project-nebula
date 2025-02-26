package project_nebula.compute_manager.project;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import project_nebula.compute_manager.project.dao.Project;
import project_nebula.compute_manager.project.dao.ProjectTag;
import project_nebula.compute_manager.project.dto.ProjectData;
import project_nebula.compute_manager.project.dto.ProjectTagMetadata;

import java.text.MessageFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectTagRepository projectTagRepository;

    public ProjectData createProject(ProjectData projectData) {
        Project newProject = ProjectMapper.toProject(projectData);
        Project savedProject = projectRepository.save(newProject);
        List<ProjectTag> tags = projectData
            .getMetadata()
            .getTags()
            .stream()
            .map(tag -> {
                ProjectTag newTag = ProjectTagMapper.toProjectTag(tag);
                newTag.setProject(savedProject);
                return newTag;
            })
            .collect(Collectors.toList());
        projectTagRepository.saveAll(tags);
        return ProjectMapper.toProjectData(newProject);
    }

    public ProjectData updateProject(UUID id, ProjectData projectData) throws NoSuchElementException {
        if (projectRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Project with id " + id + " does not exist");
        }

        Project project = ProjectMapper.toProject(projectData);
        Project savedProject = projectRepository.save(project);
        savedProject.setId(id);
        return ProjectMapper.toProjectData(savedProject);
    }

    public void deleteProject(UUID id) {
        if (projectRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Project with id " + id + " does not exist");
        }

        projectRepository.deleteById(id);
    }

    public List<ProjectData> getAllProjects(UUID userId) {
        List<Project> projects = projectRepository.findByUserId(userId);
        return projects.stream().map(ProjectMapper::toProjectData).collect(Collectors.toList());
    }

    public ProjectTagMetadata addTag(UUID projectId, ProjectTagMetadata tag) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new NoSuchElementException("Project with id " + projectId + " does not exist");
        }

        ProjectTag projectTag = ProjectTagMapper.toProjectTag(tag);
        projectTag.setProject(project.get());
        projectTagRepository.save(projectTag);
        return ProjectTagMapper.toProjectTagMetadata(projectTag);
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

        throw new NoSuchElementException(MessageFormat.format(
                "Either project with id {0} does not exist or tag with id {1} does not exist or tag with id {2} is not associated with project with id {3}",
                projectId, tagId, tagId, projectId
        ));
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
        
        throw new NoSuchElementException(MessageFormat.format(
                "Either project with id {0} does not exist or tag with id {1} does not exist or tag with id {2} is not associated with project with id {3}",
                projectId, tagId, tagId, projectId
        ));
    }

    public Optional<Project> getProjectById(UUID id) {
        return projectRepository.findById(id);
    }

}
