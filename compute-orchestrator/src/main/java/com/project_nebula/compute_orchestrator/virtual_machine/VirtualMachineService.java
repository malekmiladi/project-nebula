package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.compute_orchestrator.compute.ComputeService;
import com.project_nebula.compute_orchestrator.virtual_machine.dao.VirtualMachineInstance;
import com.project_nebula.compute_orchestrator.virtual_machine.dto.VirtualMachineRequest;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.virtual_machine_operations.VirtualMachineOperationsClient;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.*;
import com.project_nebula.shared.compute.ComputeNodeObject;
import com.project_nebula.shared.resource.VirtualMachineError;
import com.project_nebula.shared.resource.VirtualMachineErrorType;
import com.project_nebula.shared.resource.VirtualMachineMetadata;
import com.project_nebula.shared.resource.VirtualMachineState;
import com.project_nebula.shared.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VirtualMachineService {

    VirtualMachineRepository virtualMachineRepository;
    ComputeService computeService;

    public Result<VirtualMachineMetadata> createVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        ComputeNodeObject node = computeService.findNodeForVirtualMachine(virtualMachineRequest);

        if (node == null) {
            return null;
        }

        computeService.updateNodeResourcesData(node.getMetadata().getId(), virtualMachineRequest.getSpecs(), -1);
        UUID id = registerVirtualMachine(node, virtualMachineRequest);
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(id, virtualMachineRequest);
        VirtualMachineOperationResult response = client.createVM(request);

        if (response.getSuccess()) {
            VirtualMachineMetadata metadata = buildVirtualMachineMetadataFromResponseMetadata(response.getMetadata());
            virtualMachineRepository.updateStateById(id, VirtualMachineState.RUNNING);
            return Result.success(metadata);
        } else {
            virtualMachineRepository.deleteById(id);
            computeService.updateNodeResourcesData(node.getMetadata().getId(), virtualMachineRequest.getSpecs(), 1);
            VirtualMachineError error = buildVirtualMachineErrorFromResponseError(response.getError());
            return Result.failure(error);
        }
    }

    private UUID registerVirtualMachine(ComputeNodeObject node, VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = VirtualMachineInstance.builder()
                .name(virtualMachineRequest.getMetadata().getId())
                .node(computeService.getComputeNodeById(node.getMetadata().getId()))
                .state(VirtualMachineState.UNKNOWN)
                .build();
        return virtualMachineRepository.save(vm).getId();
    }

    private VirtualMachineOperationsClient createNewClient(ComputeNodeObject node) {
        GRPCClientConfiguration config = GRPCClientConfiguration.builder()
                .hostname(node.getMetadata().getHostname())
                .port(node.getMetadata().getPort())
                .tlsEnable(false)
                .build();
        return new VirtualMachineOperationsClient(config, null);
    }

    private VirtualMachine buildVirtualMachineMessageFromRequest(UUID id, VirtualMachineRequest request) {
        Specs specs = Specs.newBuilder()
                .setCpus(request.getSpecs().getVCpus())
                .setMemory(request.getSpecs().getVRamGb())
                .setStorage(request.getSpecs().getVDiskGb())
                .build();
        Image image = Image.newBuilder()
                .setSource(ImageSource.valueOf(request.getImage().getSource().name()))
                .setUrl(request.getImage().getUrl())
                .build();
        return VirtualMachine.newBuilder()
                .setId(id.toString())
                .setImage(image)
                .setSpecs(specs)
                .build();
    }

    private VirtualMachineMetadata buildVirtualMachineMetadataFromResponseMetadata(VirtualMachineMetadataDTO responseMetadata) {
        HashMap<String, String> ipAddresses = new HashMap<>();
        ipAddresses.put("ipv4", responseMetadata.getIpv4());
        ipAddresses.put("ipv6", responseMetadata.getIpv6());
        return VirtualMachineMetadata.builder()
                .ipAddresses(ipAddresses)
                .state(VirtualMachineState.valueOf(responseMetadata.getState().name()))
                .build();
    }

    private VirtualMachineError buildVirtualMachineErrorFromResponseError(VirtualMachineOperationError error) {
        return VirtualMachineError.builder()
                .message(error.getMessage())
                .type(VirtualMachineErrorType.valueOf(error.getType().name()))
                .build();
    }

    public Result<Boolean> stopVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult response = client.stopVM(request);

        if (response.getSuccess()) {
            virtualMachineRepository.updateStateById(vm.getId(), VirtualMachineState.STOPPED);
            return Result.success(true);
        } else {
            VirtualMachineError error = buildVirtualMachineErrorFromResponseError(response.getError());
            return Result.failure(error);
        }

    }

    public Result<VirtualMachineMetadata> restartVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult response = client.restartVM(request);

        return getVirtualMachineMetadataResult(vm, response);

    }

    public Result<VirtualMachineMetadata> startVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult response = client.startVM(request);

        return getVirtualMachineMetadataResult(vm, response);

    }

    private Result<VirtualMachineMetadata> getVirtualMachineMetadataResult(VirtualMachineInstance vm, VirtualMachineOperationResult response) {
        if (response.getSuccess()) {
            VirtualMachineMetadata metadata = buildVirtualMachineMetadataFromResponseMetadata(response.getMetadata());
            virtualMachineRepository.updateStateById(vm.getId(), VirtualMachineState.RUNNING);
            return Result.success(metadata);
        } else {
            VirtualMachineError error = buildVirtualMachineErrorFromResponseError(response.getError());
            return Result.failure(error);
        }
    }

    public Result<Boolean> deleteVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult response = client.deleteVM(request);

        if (response.getSuccess()) {
            computeService.updateNodeResourcesData(node.getMetadata().getId(), virtualMachineRequest.getSpecs(), 1);
            virtualMachineRepository.deleteById(vm.getId());
            return Result.success(true);
        } else {
            VirtualMachineError error = buildVirtualMachineErrorFromResponseError(response.getError());
            return Result.failure(error);
        }
    }

}
