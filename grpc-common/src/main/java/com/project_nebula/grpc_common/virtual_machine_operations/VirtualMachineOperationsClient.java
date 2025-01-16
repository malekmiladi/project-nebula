package com.project_nebula.grpc_common.virtual_machine_operations;

import com.project_nebula.grpc_common.GRPCClientConfiguration;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.VirtualMachine;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.VirtualMachineOperationResult;
import com.project_nebula.grpc_common.virtual_machine_ops.proto.VirtualMachineOperationsGrpc;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class VirtualMachineOperationsClient {
    private final VirtualMachineOperationsGrpc.VirtualMachineOperationsBlockingStub blockingStub;
    private final CallCredentials callCredentials;
    private final GRPCClientConfiguration conf;

    public VirtualMachineOperationsClient(GRPCClientConfiguration conf, CallCredentials callCredentials) {
        this.conf = conf;
        this.callCredentials = callCredentials;
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(conf.getHostname(), conf.getPort());
        if (!conf.isTlsEnable()) {
            channelBuilder.usePlaintext();
        }
        ManagedChannel channel = channelBuilder.build();
        this.blockingStub = VirtualMachineOperationsGrpc.newBlockingStub(channel);
    }

    public VirtualMachineOperationResult createVM(VirtualMachine vm) {
        if (conf.isTlsEnable()) {
            return blockingStub.withCallCredentials(callCredentials).createVM(vm);
        } else {
            return blockingStub.createVM(vm);
        }
    }

    public VirtualMachineOperationResult deleteVM(VirtualMachine vm) {
        if (conf.isTlsEnable()) {
            return blockingStub.withCallCredentials(callCredentials).deleteVM(vm);
        } else {
            return blockingStub.deleteVM(vm);
        }
    }

    public VirtualMachineOperationResult startVM(VirtualMachine vm) {
        if (conf.isTlsEnable()) {
            return blockingStub.withCallCredentials(callCredentials).startVM(vm);
        } else {
            return blockingStub.startVM(vm);
        }
    }

    public VirtualMachineOperationResult stopVM(VirtualMachine vm) {
        if (conf.isTlsEnable()) {
            return blockingStub.withCallCredentials(callCredentials).stopVM(vm);
        } else {
            return blockingStub.stopVM(vm);
        }
    }

    public VirtualMachineOperationResult restartVM(VirtualMachine vm) {
        if (conf.isTlsEnable()) {
            return blockingStub.withCallCredentials(callCredentials).restartVM(vm);
        } else {
            return blockingStub.restartVM(vm);
        }
    }

}
