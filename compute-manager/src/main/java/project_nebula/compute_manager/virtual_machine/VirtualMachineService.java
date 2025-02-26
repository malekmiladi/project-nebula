package project_nebula.compute_manager.virtual_machine;

import com.project_nebula.shared.MessageQueueConfig;
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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VirtualMachineService {

    private final VirtualMachineRepository virtualMachineRepository;
    private final ProjectService projectService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public VirtualMachineData createVirtualMachine(UUID projectId, VirtualMachineData virtualMachineData) {
        Optional<Project> optionalProject = projectService.getProjectById(projectId);

        if (optionalProject.isEmpty()) {
            throw new NoSuchElementException("Project with id " + projectId + " does not exist");
        }

        Project project = optionalProject.get();
        VirtualMachine vm = VirtualMachineMapper.toVirtualMachine(virtualMachineData);
        vm.setProject(project);
        vm.setState(VirtualMachineState.CREATING);
        VirtualMachine savedVirtualMachine = virtualMachineRepository.save(vm);
        sendConfigurationToCloudDataSource(savedVirtualMachine.getId(), virtualMachineData.getConfig());
        return VirtualMachineMapper.toVirtualMachineData(savedVirtualMachine);
    }

    public void updateMetadata(UUID id, VirtualMachineMetadata metadata) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);

        if (virtualMachine.isEmpty()) {
            return;
        }

        VirtualMachine vm = virtualMachine.get();
        HashMap<String, String> ipAddresses = metadata.getIpAddresses();
        vm.setInternalIpV4(ipAddresses.get("internalIpV4"));
        vm.setExternalIpV4(ipAddresses.get("externalIpV4"));
        vm.setInternalIpV6(ipAddresses.get("internalIpV6"));
        vm.setExternalIpV6(ipAddresses.get("externalIpV6"));
        vm.setState(metadata.getState());
        virtualMachineRepository.save(vm);
    }

    public VirtualMachineData startDeleteVirtualMachineWorkflow(UUID projectId, UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        if (virtualMachine.isEmpty()) {
            throw new NoSuchElementException("Virtual machine with id " + id + " does not exist");
        }

        VirtualMachine vm = virtualMachine.get();
        vm.setState(VirtualMachineState.DELETING);
        VirtualMachine savedVirtualMachine = virtualMachineRepository.save(vm);
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(savedVirtualMachine, null);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_START_VM_REQUEST, request);

        VirtualMachineConfigurationRequest configDeleteRequest = VirtualMachineConfigurationRequest.builder()
                .id(id)
                .build();

        kafkaTemplate.send(MessageQueueConfig.TOPIC_DELETE_VM_REQUEST, configDeleteRequest);
        return VirtualMachineMapper.toVirtualMachineData(savedVirtualMachine);
    }

    public void deleteVirtualMachine(UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);

        if (virtualMachine.isEmpty()) {
            return;
        }

        VirtualMachine vm = virtualMachine.get();
        virtualMachineRepository.delete(vm);
    }

    public void stopVirtualMachine(UUID id) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);

        if (virtualMachine.isEmpty()) {
            return;
        }

        VirtualMachine vm = virtualMachine.get();
        vm.setState(VirtualMachineState.STOPPED);
        virtualMachineRepository.save(vm);
    }

    private void sendConfigurationToCloudDataSource(UUID id, String config) {
        VirtualMachineConfigurationRequest request = VirtualMachineConfigurationRequest.builder()
                .config(config)
                .id(id)
                .build();
        kafkaTemplate.send(MessageQueueConfig.TOPIC_VM_CONFIG_SAVE_REQUEST, request);
    }

    public void sendCreateVirtualMachineRequestToOrchestrator(UUID id, String authToken) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);

        if (virtualMachine.isEmpty()) {
            return;
        }

        VirtualMachine vm = virtualMachine.get();
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, authToken);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_CREATE_VM_REQUEST, request);
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
        // TODO: add unauthorized access control (chain of responsibility filter) after implementing user & permissions features
        throw new NoSuchElementException(MessageFormat.format(
                "Either project with id {0} does not exist or virtual machine with id {1} does not exist or virtual machine with id {2} is not associated with project with id {3}",
                projectId, id, id, projectId
        ));
    }

    private VirtualMachineData runStopVirtualMachineWorkflow(UUID id, VirtualMachine vm) {
        vm.setState(VirtualMachineState.STOPPING);
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, null);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_STOP_VM_REQUEST, request);
        return VirtualMachineMapper.toVirtualMachineData(vm);
    }

    private VirtualMachineData runRestartVirtualMachineWorkflow(UUID id, VirtualMachine vm) {
        vm.setState(VirtualMachineState.RESTARTING);
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, null);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_RESTART_VM_REQUEST, request);
        return VirtualMachineMapper.toVirtualMachineData(vm);
    }

    private VirtualMachineData runStartVirtualMachineWorkflow(UUID id, VirtualMachine vm) {
        vm.setState(VirtualMachineState.STARTED);
        VirtualMachineRequest request = VirtualMachineMapper.toVirtualMachineRequest(vm, null);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_START_VM_REQUEST, request);
        return VirtualMachineMapper.toVirtualMachineData(vm);
    }

}
