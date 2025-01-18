package com.project_nebula.shared.resource;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class VirtualMachineInstanceMetadata {
    private String region;
    private UUID id;
}
