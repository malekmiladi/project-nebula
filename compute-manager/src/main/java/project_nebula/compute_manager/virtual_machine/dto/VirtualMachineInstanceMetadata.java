package project_nebula.compute_manager.virtual_machine.dto;

import com.project_nebula.shared.resource.VirtualMachineState;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineInstanceMetadata {
    private UUID userId;
    private String name;
    private String description;
    private String region;
    private VirtualMachineState state;
    private String internalIpv4Address;
    private String internalIpv6Address;
    private String externalIpv4Address;
    private String externalIpv6Address;
    private Date createdAt;
}
