package com.project_nebula.compute_node.operations;

import com.project_nebula.grpc_common.virtual_machine_ops.proto.*;
import com.project_nebula.shared.utils.Result;
import com.project_nebula.shared.resource.VirtualMachineMetadata;
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
                    .setState(State.valueOf(opResult.getValue().getState().name()))
                    .build();
            response.setMetadata(metadata);
        }
        return response
                .setSuccess(true)
                .build();
    }

    private VirtualMachineOperationResult handleOperationFailure(Result<VirtualMachineMetadata> opResult, Operation op) {
        VirtualMachineOperationError error = VirtualMachineOperationError.newBuilder()
                .setType(ErrorType.forNumber(op.ordinal()))
                .setMessage(opResult.getError().getMessage())
                .build();
        return VirtualMachineOperationResult.newBuilder()
                .setSuccess(false)
                .setError(error)
                .build();
    }
}
