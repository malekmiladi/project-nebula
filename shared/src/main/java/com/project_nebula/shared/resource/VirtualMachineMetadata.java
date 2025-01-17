package com.project_nebula.shared.resource;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Data
public class VirtualMachineMetadata {
    private final VirtualMachineState state;
    private final HashMap<String, String> ipAddresses;
}
