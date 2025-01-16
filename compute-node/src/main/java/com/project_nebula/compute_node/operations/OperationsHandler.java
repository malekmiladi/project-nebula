package com.project_nebula.compute_node.operations;

import com.project_nebula.compute_node.grpc_common.virtual_machine_ops.proto.VirtualMachineMetadataDTO;
import com.project_nebula.compute_node.grpc_common.virtual_machine_ops.proto.VirtualMachineOperationError;
import com.project_nebula.compute_node.grpc_common.virtual_machine_ops.proto.VirtualMachineOperationResult;
import com.project_nebula.hypervisor.resource.VirtualMachineMetadata;
import com.project_nebula.hypervisor.utils.Result;
import org.springframework.stereotype.Service;

@Service
public class OperationsHandler {

    public VirtualMachineOperationResult handleOperationResult(Result<VirtualMachineMetadata> opResult, Operation op) {
        if (opResult.isSuccess()) {
            return handleOperationSuccess(opResult, op);
        } else {
            return handleOperationFailure(opResult, op);
        }
    }

    private VirtualMachineOperationResult handleOperationSuccess(Result<VirtualMachineMetadata> opResult, Operation op) {
        VirtualMachineMetadataDTO metadata;
        VirtualMachineOperationResult.Builder response = VirtualMachineOperationResult.newBuilder();
        if (op == Operation.CREATE || op == Operation.RESTART || op == Operation.START) {
            metadata = VirtualMachineMetadataDTO.newBuilder()
                    .setIpv4(opResult.getValue().getIpAddresses().get("ipv4"))
                    .setIpv6(opResult.getValue().getIpAddresses().get("ipv6"))
                    .setState(opResult.getValue().getState().ordinal())
                    .build();
            response.setMetadata(metadata);
        }
        return response
                .setSuccess(true)
                .build();
    }

    private VirtualMachineOperationResult handleOperationFailure(Result<VirtualMachineMetadata> opResult, Operation op) {
        VirtualMachineOperationError error = VirtualMachineOperationError.newBuilder()
                .setType(op.ordinal())
                .setMessage(opResult.getError().getMessage())
                .build();
        return VirtualMachineOperationResult.newBuilder()
                .setSuccess(false)
                .setError(error)
                .build();
    }
}
