package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.compute_orchestrator.compute.ComputeService;
import com.project_nebula.compute_orchestrator.virtual_machine.dao.VirtualMachineInstance;
import com.project_nebula.compute_orchestrator.virtual_machine.dto.VirtualMachineRequest;
import com.project_nebula.compute_orchestrator.virtual_machine.dto.VirtualMachineResponse;
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

    public Result<VirtualMachineResponse> createVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        ComputeNodeObject node = computeService.findNodeForVirtualMachine(virtualMachineRequest);

        if (node == null) {
            return null;
        }

        computeService.updateNodeResourcesData(node.getMetadata().getId(), virtualMachineRequest.getSpecs(), -1);
        UUID id = registerVirtualMachine(node, virtualMachineRequest);
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(id, virtualMachineRequest);
        VirtualMachineOperationResult nodeResponse = client.createVM(request);

        VirtualMachineResponse response = buildVirtualMachineResponseFromComputeResponse(nodeResponse, virtualMachineRequest);

        if (nodeResponse.getSuccess()) {
            virtualMachineRepository.updateStateById(id, VirtualMachineState.RUNNING);
        } else {
            virtualMachineRepository.deleteById(id);
            computeService.updateNodeResourcesData(node.getMetadata().getId(), virtualMachineRequest.getSpecs(), 1);
        }
        return Result.success(response);
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

    public Result<VirtualMachineResponse> stopVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult nodeResponse = client.stopVM(request);
        VirtualMachineResponse response = buildVirtualMachineResponseFromComputeResponse(nodeResponse, virtualMachineRequest);

        if (nodeResponse.getSuccess()) {
            virtualMachineRepository.updateStateById(vm.getId(), VirtualMachineState.STOPPED);
        }

        return Result.success(response);
    }

    public Result<VirtualMachineResponse> restartVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult nodeResponse = client.restartVM(request);

        VirtualMachineResponse response = buildVirtualMachineResponseFromComputeResponse(nodeResponse, virtualMachineRequest);

        if (nodeResponse.getSuccess()) {
            virtualMachineRepository.updateStateById(vm.getId(), VirtualMachineState.RUNNING);
        }

        return Result.success(response);

    }

    public Result<VirtualMachineResponse> startVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult nodeResponse = client.startVM(request);

        VirtualMachineResponse response = buildVirtualMachineResponseFromComputeResponse(nodeResponse, virtualMachineRequest);

        if (nodeResponse.getSuccess()) {
            virtualMachineRepository.updateStateById(vm.getId(), VirtualMachineState.RUNNING);
        }

        return Result.success(response);

    }

    public Result<VirtualMachineResponse> deleteVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        VirtualMachineInstance vm = virtualMachineRepository.findOneByName(virtualMachineRequest.getMetadata().getId());
        ComputeNodeObject node = computeService.buildComputeNodeObjectFromComputeNode(vm.getNode());
        VirtualMachineOperationsClient client = createNewClient(node);
        VirtualMachine request = buildVirtualMachineMessageFromRequest(vm.getId(), virtualMachineRequest);
        VirtualMachineOperationResult nodeResponse = client.deleteVM(request);

        VirtualMachineResponse response = buildVirtualMachineResponseFromComputeResponse(nodeResponse, virtualMachineRequest);

        if (nodeResponse.getSuccess()) {
            computeService.updateNodeResourcesData(node.getMetadata().getId(), virtualMachineRequest.getSpecs(), 1);
            virtualMachineRepository.deleteById(vm.getId());
        }

        return Result.success(response);
    }

    public VirtualMachineResponse buildVirtualMachineResponseFromComputeResponse(VirtualMachineOperationResult nodeResponse, VirtualMachineRequest request) {
        if (nodeResponse.getSuccess()) {
            VirtualMachineMetadata metadata = buildVirtualMachineMetadataFromResponseMetadata(nodeResponse.getMetadata());
            return VirtualMachineResponse.builder()
                    .id(request.getMetadata().getId())
                    .metadata(metadata)
                    .build();
        } else {
            VirtualMachineError error = buildVirtualMachineErrorFromResponseError(nodeResponse.getError());
            return VirtualMachineResponse.builder()
                    .id(request.getMetadata().getId())
                    .error(error)
                    .build();
        }
    }

}
