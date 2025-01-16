package com.project_nebula.compute_node.operations;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.api.HypervisorInterfaceFactory;
import com.project_nebula.compute_node.grpc.virtual_machine_ops.proto.*;
import com.project_nebula.hypervisor.HypervisorInterface;
import com.project_nebula.hypervisor.resource.VirtualMachineMetadata;
import com.project_nebula.hypervisor.resource.VirtualMachineSpecs;
import com.project_nebula.hypervisor.resource.image.ImageMetadata;
import com.project_nebula.hypervisor.resource.image.ImageSource;
import com.project_nebula.hypervisor.utils.Result;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VirtualMachineOperationsService extends VirtualMachineOperationsGrpc.VirtualMachineOperationsImplBase {

    HypervisorInterface hypervisorInterface;
    OperationsHandler operationsHandler;
    ComputeConfiguration conf;

    public VirtualMachineOperationsService(ComputeConfiguration conf, OperationsHandler operationsHandler) {
        this.conf = conf;
        HypervisorInterfaceFactory factory = new HypervisorInterfaceFactory(conf);
        hypervisorInterface = factory.getHypervisorInterface();
        this.operationsHandler = operationsHandler;
    }

    @Override
    public void createVM(VirtualMachine vm, StreamObserver<VirtualMachineOperationResult> responseObserver) {
        Specs specs = vm.getSpecs();
        AuthCredentials credentials = vm.getAuthCredentials();
        Image image = vm.getImage();
        ImageSource source = image.getSource().getNumber() == 0 ? ImageSource.LOCAL : ImageSource.ONLINE;

        log.info("Creating virtual machine { id: {}, cpus: {}, memory: {}GB, disk: {}GB, image: { source: {}, url: {} } }", vm.getId(), specs.getCpus(), specs.getMemory(), specs.getStorage(), source.name(), image.getUrl());

        Result<VirtualMachineMetadata> opResult = hypervisorInterface.createVM(
                vm.getId(),
                new VirtualMachineSpecs(specs.getCpus(), specs.getMemory(), specs.getStorage()),
                new ImageMetadata(image.getUrl(), source),
                conf.getCloudDatasourceUri()
        );

        VirtualMachineOperationResult response = operationsHandler.handleOperationResult(opResult, Operation.CREATE);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteVM(VirtualMachine vm, StreamObserver<VirtualMachineOperationResult> responseObserver) {
        log.info("Deleting virtual machine \"{}\"", vm.getId());
        Result<VirtualMachineMetadata> opResult = hypervisorInterface.deleteVM(vm.getId());
        VirtualMachineOperationResult response = operationsHandler.handleOperationResult(opResult, Operation.DELETE);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void stopVM(VirtualMachine vm, StreamObserver<VirtualMachineOperationResult> responseObserver) {
        log.info("Stopping virtual machine \"{}\"", vm.getId());
        Result<VirtualMachineMetadata> opResult = hypervisorInterface.stopVM(vm.getId());
        VirtualMachineOperationResult response = operationsHandler.handleOperationResult(opResult, Operation.STOP);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void restartVM(VirtualMachine vm, StreamObserver<VirtualMachineOperationResult> responseObserver) {
        log.info("Restarting virtual machine \"{}\"", vm.getId());
        Result<VirtualMachineMetadata> opResult = hypervisorInterface.restartVM(vm.getId());
        VirtualMachineOperationResult response = operationsHandler.handleOperationResult(opResult, Operation.RESTART);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void startVM(VirtualMachine vm, StreamObserver<VirtualMachineOperationResult> responseObserver) {
        log.info("Starting virtual machine \"{}\"", vm.getId());
        Result<VirtualMachineMetadata> opResult = hypervisorInterface.startVM(vm.getId());
        VirtualMachineOperationResult response = operationsHandler.handleOperationResult(opResult, Operation.START);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
