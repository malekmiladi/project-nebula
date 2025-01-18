package project_nebula.compute_manager.virtual_machine;

import com.project_nebula.shared.resource.VirtualMachineMetadata;
import com.project_nebula.shared.resource.VirtualMachineState;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import project_nebula.compute_manager.project.ProjectService;
import project_nebula.compute_manager.project.dao.Project;
import project_nebula.compute_manager.virtual_machine.dao.VirtualMachine;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineData;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VirtualMachineService {

    VirtualMachineRepository virtualMachineRepository;
    ProjectService projectService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public VirtualMachineData createVirtualMachine(UUID projectId, VirtualMachineData virtualMachineData) {
        Optional<Project> optionalProject = projectService.getProjectById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            VirtualMachine vm = VirtualMachineMapper.toVirtualMachine(virtualMachineData);
            vm.setProject(project);
            vm.setState(VirtualMachineState.CREATING);
            VirtualMachine savedVirtualMachine = virtualMachineRepository.save(vm);
            return VirtualMachineMapper.toVirtualMachineData(savedVirtualMachine);
        }
        throw new NoSuchElementException("Project with id " + projectId + " does not exist");
    }

    public void updateMetadata(UUID id, VirtualMachineMetadata metadata) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        if (virtualMachine.isPresent()) {
            VirtualMachine vm = virtualMachine.get();
            HashMap<String, String> ipAddresses = metadata.getIpAddresses();
            vm.setInternalIpV4(ipAddresses.get("internalIpV4"));
            vm.setExternalIpV4(ipAddresses.get("externalIpV4"));
            vm.setInternalIpV6(ipAddresses.get("internalIpV6"));
            vm.setExternalIpV6(ipAddresses.get("externalIpV6"));
            vm.setState(metadata.getState());
            virtualMachineRepository.save(vm);
        }
    }

    public void deleteVirtualMachine(UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        virtualMachine.ifPresent(machine -> virtualMachineRepository.delete(machine));
    }

    public void stopVirtualMachine(UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        if (virtualMachine.isPresent()) {
            VirtualMachine vm = virtualMachine.get();
            vm.setState(VirtualMachineState.STOPPED);
            virtualMachineRepository.save(vm);
        }
    }

}
