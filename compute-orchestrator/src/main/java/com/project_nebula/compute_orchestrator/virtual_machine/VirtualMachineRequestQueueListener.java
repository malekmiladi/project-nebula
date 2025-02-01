package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.shared.MessageQueueConfig;
import com.project_nebula.shared.resource.VirtualMachineRequest;
import com.project_nebula.shared.resource.VirtualMachineResponse;
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

    @KafkaListener(
        groupId = MessageQueueConfig.GROUP_ID,
        topics = MessageQueueConfig.TOPIC_CREATE_VM_REQUEST,
        properties = {MessageQueueConfig.VM_OPERATION_REQUEST_TYPE_MAPPING}
    )
    public void createVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.createVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_CREATE_VM_RESPONSE, response.getValue());
    }

    @KafkaListener(
        groupId = MessageQueueConfig.GROUP_ID,
        topics = MessageQueueConfig.TOPIC_STOP_VM_REQUEST,
        properties = {MessageQueueConfig.VM_OPERATION_REQUEST_TYPE_MAPPING}
    )
    public void stopVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.stopVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_STOP_VM_RESPONSE, response.getValue());
    }

    @KafkaListener(
        groupId = MessageQueueConfig.GROUP_ID,
        topics = MessageQueueConfig.TOPIC_DELETE_VM_REQUEST,
        properties = {MessageQueueConfig.VM_OPERATION_REQUEST_TYPE_MAPPING}
    )
    public void deleteVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.deleteVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_DELETE_VM_RESPONSE, response.getValue());
    }

    @KafkaListener(
        groupId = MessageQueueConfig.GROUP_ID,
        topics = MessageQueueConfig.TOPIC_START_VM_REQUEST,
        properties = {MessageQueueConfig.VM_OPERATION_REQUEST_TYPE_MAPPING}
    )
    public void startVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.startVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_START_VM_RESPONSE, response.getValue());
    }

    @KafkaListener(
        groupId = MessageQueueConfig.GROUP_ID,
        topics = MessageQueueConfig.TOPIC_RESTART_VM_REQUEST,
        properties = {MessageQueueConfig.VM_OPERATION_REQUEST_TYPE_MAPPING}
    )
    public void restartVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        Result<VirtualMachineResponse> response = virtualMachineService.restartVirtualMachine(virtualMachineRequest);
        kafkaTemplate.send(MessageQueueConfig.TOPIC_RESTART_VM_RESPONSE, response.getValue());
    }

}
