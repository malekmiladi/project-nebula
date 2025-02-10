package com.project_nebula.grpc_common.virtual_machine_operations;

import com.project_nebula.grpc_common.BlockingStubFactory;
import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.VirtualMachine;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.VirtualMachineOperationResult;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.VirtualMachineOperationsGrpc;

public class VirtualMachineOperationsClient {
    private final VirtualMachineOperationsGrpc.VirtualMachineOperationsBlockingStub blockingStub;

    public VirtualMachineOperationsClient(GRPCClientConfiguration conf) {
        this.blockingStub = (VirtualMachineOperationsGrpc.VirtualMachineOperationsBlockingStub) BlockingStubFactory
                .createStub(VirtualMachineOperationsGrpc.class, conf);
    }

    public VirtualMachineOperationResult createVM(VirtualMachine vm) {
        return blockingStub.createVM(vm);
    }

    public VirtualMachineOperationResult deleteVM(VirtualMachine vm) {
        return blockingStub.deleteVM(vm);
    }

    public VirtualMachineOperationResult startVM(VirtualMachine vm) {
        return blockingStub.startVM(vm);
    }

    public VirtualMachineOperationResult stopVM(VirtualMachine vm) {
        return blockingStub.stopVM(vm);
    }

    public VirtualMachineOperationResult restartVM(VirtualMachine vm) {
        return blockingStub.restartVM(vm);
    }

}
