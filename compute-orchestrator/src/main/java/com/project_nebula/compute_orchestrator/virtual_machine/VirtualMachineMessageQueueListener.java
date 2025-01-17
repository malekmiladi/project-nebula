package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.compute_orchestrator.compute.ComputeService;
import com.project_nebula.compute_orchestrator.virtual_machine.dto.VirtualMachineRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VirtualMachineMessageQueueListener {

    private final VirtualMachineService virtualMachineService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "create-virtual-machine")
    public void createVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        virtualMachineService.createVirtualMachine(virtualMachineRequest);
    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "stop-virtual-machine")
    public void stopVirtualMachine(VirtualMachineRequest virtualMachineRequest) {

    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "delete-virtual-machine")
    public void deleteVirtualMachine(VirtualMachineRequest virtualMachineRequest) {

    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "start-virtual-machine")
    public void startVirtualMachine(VirtualMachineRequest virtualMachineRequest) {

    }

    @KafkaListener(groupId = "project-nebula-virtual-machine", topics = "restart-virtual-machine")
    public void restartVirtualMachine(VirtualMachineRequest virtualMachineRequest) {

    }

}
