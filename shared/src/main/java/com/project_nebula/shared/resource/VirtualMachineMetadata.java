package com.project_nebula.shared.resource;

import lombok.*;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VirtualMachineMetadata {
    private VirtualMachineState state;
    private HashMap<String, String> ipAddresses;
}
