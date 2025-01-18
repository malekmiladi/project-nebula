package project_nebula.compute_manager.project;

import project_nebula.compute_manager.project.dao.Project;
import project_nebula.compute_manager.project.dao.ProjectTag;
import project_nebula.compute_manager.project.dto.ProjectData;
import project_nebula.compute_manager.project.dto.ProjectMetadata;
import project_nebula.compute_manager.project.dto.ProjectTagMetadata;
import project_nebula.compute_manager.virtual_machine.VirtualMachineMapper;
import project_nebula.compute_manager.virtual_machine.dao.VirtualMachine;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineData;

import java.util.ArrayList;
import java.util.List;

public class ProjectMapper {
    public static Project toProject(ProjectData projectData) {

        List<ProjectTag> tags = new ArrayList<>();
        for (ProjectTagMetadata tag : projectData.getMetadata().getTags()) {
            tags.add(
                ProjectTagMapper.toProjectTag(tag)
            );
        }

        List<VirtualMachine> virtualMachines = new ArrayList<>();
        for (VirtualMachineData virtualMachine : projectData.getVirtualMachines()) {
            virtualMachines.add(
                    VirtualMachineMapper.toVirtualMachine(virtualMachine)
            );
        }

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

        List<ProjectTagMetadata> tags = new ArrayList<>();
        for (ProjectTag tag : project.getTags()) {
            tags.add(
                ProjectTagMapper.toProjectTagMetadata(tag)
            );
        }

        List<VirtualMachineData> virtualMachines = new ArrayList<>();
        for (VirtualMachine virtualMachine : project.getInstances()) {
            virtualMachines.add(
                    VirtualMachineMapper.toVirtualMachineData(virtualMachine)
            );
        }

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
