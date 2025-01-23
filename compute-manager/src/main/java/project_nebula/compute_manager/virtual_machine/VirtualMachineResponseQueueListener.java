package project_nebula.compute_manager.virtual_machine;

import com.project_nebula.shared.MessageQueueConfig;
import com.project_nebula.shared.resource.VirtualMachineConfigurationResponse;
import com.project_nebula.shared.resource.VirtualMachineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VirtualMachineResponseQueueListener {

    private final VirtualMachineService virtualMachineService;

    @KafkaListener(
        groupId = MessageQueueConfig.GROUP_ID,
        topics = {
            MessageQueueConfig.TOPIC_CREATE_VM_RESPONSE,
            MessageQueueConfig.TOPIC_START_VM_RESPONSE,
            MessageQueueConfig.TOPIC_RESTART_VM_RESPONSE
        }
    )
    public void updateVirtualMachineMetadata(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.updateMetadata(virtualMachineResponse.getId(), virtualMachineResponse.getMetadata());
        }
    }

    @KafkaListener(groupId = MessageQueueConfig.GROUP_ID, topics = MessageQueueConfig.TOPIC_DELETE_VM_RESPONSE)
    public void deleteVirtualMachine(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.deleteVirtualMachine(virtualMachineResponse.getId());
        }
    }

    @KafkaListener(groupId = MessageQueueConfig.GROUP_ID, topics = MessageQueueConfig.TOPIC_STOP_VM_RESPONSE)
    public void stopVirtualMachine(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.stopVirtualMachine(virtualMachineResponse.getId());
        }
    }

    @KafkaListener(
        groupId = MessageQueueConfig.GROUP_ID,
        topics = MessageQueueConfig.TOPIC_VM_CONFIG_SAVE_RESPONSE,
        properties = {MessageQueueConfig.VM_CONFIG_SAVE_RESPONSE_TYPE_MAPPING}
    )
    public void sendCreateVirtualMachineRequestToOrchestrator(VirtualMachineConfigurationResponse response) {
        virtualMachineService.sendCreateVirtualMachineRequestToOrchestrator(response.getId(), response.getAuthToken());
    }

}
