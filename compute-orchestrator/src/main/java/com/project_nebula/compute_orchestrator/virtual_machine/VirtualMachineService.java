package com.project_nebula.compute_orchestrator.virtual_machine;

import com.project_nebula.compute_orchestrator.compute.ComputeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VirtualMachineService {

    private final VirtualMachineRepository virtualMachineRepository;
    private final ComputeRepository computeRepository;

    public VirtualMachineService(VirtualMachineRepository virtualMachineRepository, ComputeRepository computeRepository) {
        this.virtualMachineRepository = virtualMachineRepository;
        this.computeRepository = computeRepository;
    }

}
