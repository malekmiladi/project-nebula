package project_nebula.compute_manager.virtual_machine;

import com.project_nebula.shared.resource.VirtualMachineRequest;
import com.project_nebula.shared.resource.VirtualMachineRequestMetadata;
import com.project_nebula.shared.resource.VirtualMachineSpecs;
import com.project_nebula.shared.resource.image.ImageMetadata;
import project_nebula.compute_manager.Image.ImageMapper;
import project_nebula.compute_manager.virtual_machine.dao.VirtualMachine;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineData;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineInstanceMetadata;

import java.sql.Date;

public class VirtualMachineMapper {
    public static VirtualMachineData toVirtualMachineData(VirtualMachine vm) {
        VirtualMachineInstanceMetadata metadata = VirtualMachineInstanceMetadata.builder()
                .userId(vm.getUserId())
                .name(vm.getName())
                .description(vm.getDescription())
                .region(vm.getRegion())
                .state(vm.getState())
                .internalIpv4Address(vm.getInternalIpV4())
                .externalIpv4Address(vm.getExternalIpV4())
                .internalIpv6Address(vm.getInternalIpV6())
                .externalIpv6Address(vm.getExternalIpV6())
                .build();
        VirtualMachineSpecs specs = VirtualMachineSpecs.builder()
                .disk(vm.getDisk())
                .memory(vm.getMemory())
                .cpus(vm.getCpus())
                .build();
        return VirtualMachineData.builder()
                .image(ImageMapper.toImageData(vm.getImage()))
                .id(vm.getId())
                .specs(specs)
                .metadata(metadata)
                .build();
    }

    public static VirtualMachine toVirtualMachine(VirtualMachineData vmData) {
        return VirtualMachine.builder()
                .id(vmData.getId())
                .userId(vmData.getMetadata().getUserId())
                .createdAt((Date) vmData.getMetadata().getCreatedAt())
                .region(vmData.getMetadata().getRegion())
                .cpus(vmData.getSpecs().getCpus())
                .memory(vmData.getSpecs().getMemory())
                .disk(vmData.getSpecs().getDisk())
                .name(vmData.getMetadata().getName())
                .description(vmData.getMetadata().getDescription())
                .internalIpV4(vmData.getMetadata().getInternalIpv4Address())
                .externalIpV4(vmData.getMetadata().getExternalIpv4Address())
                .internalIpV6(vmData.getMetadata().getInternalIpv6Address())
                .externalIpV6(vmData.getMetadata().getExternalIpv6Address())
                .image(ImageMapper.toImage(vmData.getImage()))
                .state(vmData.getMetadata().getState())
                .build();
    }

    public static VirtualMachineRequest toVirtualMachineRequest(VirtualMachine vm, String authToken) {
        VirtualMachineRequestMetadata metadata = VirtualMachineRequestMetadata.builder()
                .region(vm.getRegion())
                .id(vm.getId())
                .state(vm.getState())
                .authToken(authToken)
                .build();
        VirtualMachineSpecs specs = VirtualMachineSpecs.builder()
                .cpus(vm.getCpus())
                .memory(vm.getMemory())
                .disk(vm.getDisk())
                .build();
        ImageMetadata image = ImageMetadata.builder()
                .source(vm.getImage().getSource())
                .url(vm.getImage().getUrl())
                .build();
        return VirtualMachineRequest.builder()
                .metadata(metadata)
                .specs(specs)
                .image(image)
                .build();
    }

}
