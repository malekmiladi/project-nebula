package project_nebula.compute_manager;

import com.project_nebula.shared.resource.VirtualMachineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import project_nebula.compute_manager.virtual_machine.VirtualMachineService;

@Service
@RequiredArgsConstructor
public class VirtualMachineResponseQueueListener {

    VirtualMachineService virtualMachineService;

    @KafkaListener(
        id = "project-nebula-virtual-machine",
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

    @KafkaListener(id = "project-nebula-virtual-machine", topics = "delete-virtual-machine-responses")
    public void deleteVirtualMachine(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.deleteVirtualMachine(virtualMachineResponse.getId());
        }
    }

    @KafkaListener(id = "project-nebula-virtual-machine", topics = "stop-virtual-machine-responses")
    public void stopVirtualMachine(VirtualMachineResponse virtualMachineResponse) {
        if (virtualMachineResponse.getError() == null) {
            virtualMachineService.stopVirtualMachine(virtualMachineResponse.getId());
        }
    }

}
