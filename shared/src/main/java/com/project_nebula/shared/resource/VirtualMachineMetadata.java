package com.project_nebula.shared.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VirtualMachineMetadata {
    private VirtualMachineState state;
    private HashMap<String, String> ipAddresses;
}
