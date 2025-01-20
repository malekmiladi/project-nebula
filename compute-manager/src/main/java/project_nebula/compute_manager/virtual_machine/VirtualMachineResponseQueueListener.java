package project_nebula.compute_manager.virtual_machine;

import com.project_nebula.shared.resource.VirtualMachineConfigurationResponse;
import com.project_nebula.shared.resource.VirtualMachineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VirtualMachineResponseQueueListener {

    VirtualMachineService virtualMachineService;

    @KafkaListener(
            groupId = "project-nebula-virtual-machine",
        topics = {
            "create-virtual-machine-requests",
            "start-virtual-machine-requests",
            "restart-virtual-machine-requests"
        }
    )
    public void updateVirtualMachineMetadata(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.updateMetadata(virtualMachineResponse.getId(), virtualMachineResponse.getMetadata());
        }
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "delete-virtual-machine-responses")
    public void deleteVirtualMachine(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.deleteVirtualMachine(virtualMachineResponse.getId());
        }
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "stop-virtual-machine-responses")
    public void stopVirtualMachine(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.stopVirtualMachine(virtualMachineResponse.getId());
        }
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "save-virtual-machine-config-responses")
    public void sendCreateVirtualMachineRequestToOrchestrator(VirtualMachineConfigurationResponse response) {
        virtualMachineService.sendCreateVirtualMachineRequestToOrchestrator(response.getId(), response.getAuthToken());
    }

}
