package com.project_nebula.hypervisor.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class VirtualMachineMetadata {
    private VirtualMachineState state;
    private HashMap<String, String> ipAddresses;
}
