package project_nebula.compute_manager.virtual_machine;

import com.project_nebula.shared.resource.VirtualMachineConfigurationRequest;
import com.project_nebula.shared.resource.VirtualMachineMetadata;
import com.project_nebula.shared.resource.VirtualMachineRequest;
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
            sendConfigurationToCloudDataSource(savedVirtualMachine.getId(), virtualMachineData.getConfig());
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

    public VirtualMachineData startDeleteVirtualMachineWorkflow(UUID projectId, UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        if (virtualMachine.isPresent()) {
            VirtualMachine vm = virtualMachine.get();
            vm.setState(VirtualMachineState.DELETING);
            VirtualMachine savedVirtualMachine = virtualMachineRepository.save(vm);
            VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(savedVirtualMachine, null);
            kafkaTemplate.send("delete-virtual-machine-requests", request);

            VirtualMachineConfigurationRequest configDeleteRequest = VirtualMachineConfigurationRequest.builder()
                    .id(id)
                    .build();
            kafkaTemplate.send("delete-virtual-machine-config-requests", configDeleteRequest);
            return VirtualMachineMapper.toVirtualMachineData(savedVirtualMachine);
        }
        throw new NoSuchElementException("Virtual machine with id " + id + " does not exist");
    }

    public void deleteVirtualMachine(UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        if (virtualMachine.isPresent()) {
            VirtualMachine vm = virtualMachine.get();
            virtualMachineRepository.delete(vm);
        }
    }

    public void stopVirtualMachine(UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        if (virtualMachine.isPresent()) {
            VirtualMachine vm = virtualMachine.get();
            vm.setState(VirtualMachineState.STOPPED);
            virtualMachineRepository.save(vm);
        }
    }

    private void sendConfigurationToCloudDataSource(UUID id, String config) {
        VirtualMachineConfigurationRequest request = VirtualMachineConfigurationRequest.builder()
                .config(config)
                .id(id)
                .build();
        kafkaTemplate.send("save-virtual-machine-config-requests", request);
    }

    public void sendCreateVirtualMachineRequestToOrchestrator(UUID id, String authToken) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        if (virtualMachine.isPresent()) {
            VirtualMachine vm = virtualMachine.get();
            VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, authToken);
            kafkaTemplate.send("create-virtual-machine-requests", request);
        }
    }


    public VirtualMachineData changeVirtualMachineState(UUID projectId, UUID id, VirtualMachineData virtualMachineData) {
        Optional<Project> optionalProject = projectService.getProjectById(projectId);
        Optional<VirtualMachine> optionalVirtualMachine = virtualMachineRepository.findById(id);
        if (optionalProject.isPresent() && optionalVirtualMachine.isPresent()) {
            VirtualMachine virtualMachine = optionalVirtualMachine.get();
            if (virtualMachine.getProject().getId().equals(projectId)) {
                switch (virtualMachineData.getMetadata().getState()) {
                    case STOPPED: {
                        return runStopVirtualMachineWorkflow(id, virtualMachine);
                    }
                    case RUNNING: {
                        return runRestartVirtualMachineWorkflow(id, virtualMachine);
                    }
                    case STARTED: {
                        return runStartVirtualMachineWorkflow(id, virtualMachine);
                    }
                }
            }
        }
        throw new NoSuchElementException("Either project with id " + projectId + " does not exist" +
                "or virtual machine with id " + id + " does not exist" +
                "or virtual machine with id " + id + " does not belong to project with id " + projectId);
    }

    private VirtualMachineData runStopVirtualMachineWorkflow(UUID id, VirtualMachine vm) {
        vm.setState(VirtualMachineState.STOPPING);
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, null);
        kafkaTemplate.send("stop-virtual-machine-requests", request);
        return VirtualMachineMapper.toVirtualMachineData(vm);
    }

    private VirtualMachineData runRestartVirtualMachineWorkflow(UUID id, VirtualMachine vm) {
        vm.setState(VirtualMachineState.RESTARTING);
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, null);
        kafkaTemplate.send("restart-virtual-machine-requests", request);
        return VirtualMachineMapper.toVirtualMachineData(vm);
    }

    private VirtualMachineData runStartVirtualMachineWorkflow(UUID id, VirtualMachine vm) {
        vm.setState(VirtualMachineState.STARTED);
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, null);
        kafkaTemplate.send("start-virtual-machine-requests", request);
        return VirtualMachineMapper.toVirtualMachineData(vm);
    }

}
