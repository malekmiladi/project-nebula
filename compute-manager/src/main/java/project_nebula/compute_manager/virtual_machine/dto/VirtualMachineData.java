package project_nebula.compute_manager.virtual_machine.dto;

import com.project_nebula.shared.resource.VirtualMachineSpecs;
import lombok.*;
import project_nebula.compute_manager.Image.dto.ImageData;

import java.util.UUID;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineData {

    private UUID id;
    private VirtualMachineMetadata metadata;
    private VirtualMachineSpecs specs;
    private ImageData image;

}
