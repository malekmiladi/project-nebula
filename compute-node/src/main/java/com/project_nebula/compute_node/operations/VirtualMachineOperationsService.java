package com.project_nebula.compute_node.operations;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.api.HypervisorInterfaceFactory;
import com.project_nebula.compute_node.grpc.virtual_machine_ops.proto.Specs;
import com.project_nebula.compute_node.grpc.virtual_machine_ops.proto.VirtualMachine;
import com.project_nebula.compute_node.grpc.virtual_machine_ops.proto.VirtualMachineOperationResult;
import com.project_nebula.compute_node.grpc.virtual_machine_ops.proto.VirtualMachineOperationsGrpc;
import com.project_nebula.hypervisor.HypervisorInterface;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class VirtualMachineOperationsService extends VirtualMachineOperationsGrpc.VirtualMachineOperationsImplBase {

    HypervisorInterface hypervisorInterface;

    public VirtualMachineOperationsService(ComputeConfiguration conf) {
        HypervisorInterfaceFactory factory = new HypervisorInterfaceFactory(conf);
        hypervisorInterface = factory.getHypervisorInterface();
    }

    @Override
    public void createVM(VirtualMachine vm, StreamObserver<VirtualMachineOperationResult> responseObserver) {
        Specs specs = vm.getSpecs();
        log.info("Creating virtual machine: {} with {} vCPUs, {} vRAM, {} vDISK.", specs.getName(), specs.getCpus(), specs.getMemory(), specs.getStorage());
    }

}
