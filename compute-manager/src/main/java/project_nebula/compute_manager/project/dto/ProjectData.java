package project_nebula.compute_manager.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectData {

    private UUID id;
    private ProjectMetadata metadata;

    @Builder.Default
    private List<VirtualMachineData> virtualMachines = new ArrayList<>();

}
