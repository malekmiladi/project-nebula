package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.compute_orchestrator.virtual_machine.dto.VirtualMachineRequest;
import com.project_nebula.compute_orchestrator.virtual_machine.dto.VirtualMachineResponse;
import com.project_nebula.shared.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VirtualMachineRequestQueueListener {

    private final VirtualMachineService virtualMachineService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "create-virtual-machine-requests")
    public void createVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.createVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send("create-virtual-machine-responses", response.getValue());
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "stop-virtual-machine-requests")
    public void stopVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.stopVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send("stop-virtual-machine-responses", response.getValue());
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "delete-virtual-machine-requests")
    public void deleteVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.deleteVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send("delete-virtual-machine-responses", response.getValue());
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "start-virtual-machine-requests")
    public void startVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.startVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send("start-virtual-machine-responses", response.getValue());
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "restart-virtual-machine-requests")
    public void restartVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.restartVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send("restart-virtual-machine-responses", response.getValue());
    }

}
