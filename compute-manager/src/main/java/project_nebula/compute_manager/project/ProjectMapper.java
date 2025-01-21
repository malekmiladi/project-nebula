package project_nebula.compute_manager.project;

import project_nebula.compute_manager.project.dao.Project;
import project_nebula.compute_manager.project.dao.ProjectTag;
import project_nebula.compute_manager.project.dto.ProjectData;
import project_nebula.compute_manager.project.dto.ProjectMetadata;
import project_nebula.compute_manager.project.dto.ProjectTagMetadata;
import project_nebula.compute_manager.virtual_machine.VirtualMachineMapper;
import project_nebula.compute_manager.virtual_machine.dao.VirtualMachine;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineData;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {
    public static Project toProject(ProjectData projectData) {

        List<ProjectTag> tags = projectData
            .getMetadata()
            .getTags()
            .stream()
            .map(ProjectTagMapper::toProjectTag)
            .collect(Collectors.toList());

        List<VirtualMachine> virtualMachines = projectData
            .getVirtualMachines()
            .stream()
            .map(VirtualMachineMapper::toVirtualMachine)
            .collect(Collectors.toList());

        return Project.builder()
            .id(projectData.getId())
            .userId(projectData.getMetadata().getUserId())
            .name(projectData.getMetadata().getName())
            .description(projectData.getMetadata().getDescription())
            .tags(tags)
            .instances(virtualMachines)
            .build();
    }

    public static ProjectData toProjectData(Project project) {

        List<ProjectTagMetadata> tags = project
            .getTags()
            .stream()
            .map(ProjectTagMapper::toProjectTagMetadata)
            .collect(Collectors.toList());

        List<VirtualMachineData> virtualMachines = project
            .getInstances()
            .stream()
            .map(VirtualMachineMapper::toVirtualMachineData)
            .collect(Collectors.toList());

        ProjectMetadata metadata = ProjectMetadata.builder()
            .userId(project.getUserId())
            .name(project.getName())
            .description(project.getDescription())
            .tags(tags)
            .build();

        return ProjectData.builder()
            .id(project.getId())
            .metadata(metadata)
            .virtualMachines(virtualMachines)
            .build();
    }

}
