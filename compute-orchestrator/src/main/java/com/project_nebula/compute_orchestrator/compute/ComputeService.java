package com.project_nebula.compute_orchestrator.compute;

import com.project_nebula.compute_orchestrator.compute.dao.ComputeNode;
import com.project_nebula.shared.resource.VirtualMachineRequest;
import com.project_nebula.grpc_common.heartbeat.proto.Heartbeat;
import com.project_nebula.grpc_common.orchestrator_registration.proto.RegistrationParameters;
import com.project_nebula.shared.compute.ComputeNodeMetadata;
import com.project_nebula.shared.compute.ComputeNodeObject;
import com.project_nebula.shared.compute.ComputeNodeSpecs;
import com.project_nebula.shared.compute.ComputeNodeState;
import com.project_nebula.shared.resource.VirtualMachineSpecs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ComputeService {

    private final ComputeRepository computeRepository;

    private ComputeNode buildComputeNodeFromParams(RegistrationParameters params) {
        return ComputeNode.builder()
                .defaultCpus(params.getSpecs().getCpus())
                .defaultMemory(params.getSpecs().getMemory())
                .defaultStorage(params.getSpecs().getStorage())
                .cpus(params.getSpecs().getCpus())
                .memory(params.getSpecs().getMemory())
                .storage(params.getSpecs().getStorage())
                .hostname(params.getMetadata().getHostname())
                .port(params.getMetadata().getPort())
                .id(UUID.fromString(params.getMetadata().getId()))
                .region(params.getMetadata().getRegion())
                .state(ComputeNodeState.valueOf(params.getMetadata().getState()))
                .build();
    }

    public UUID register(RegistrationParameters params) {
        ComputeNode node = buildComputeNodeFromParams(params);
        UUID id = computeRepository.existsById(node.getId()) ? node.getId() : computeRepository.save(node).getId();
        node.setId(id);
        log.info("Registered new Compute Node: {}", node);
        return id;
    }

    public boolean recordHeartbeat(Heartbeat heartbeat) {
        UUID id = UUID.fromString(heartbeat.getId());
        if (!computeRepository.existsById(id)) {
            return false;
        }
        ComputeNode node = computeRepository.getComputeNodeById(id);
        node.setState(ComputeNodeState.ACTIVE);
        node.setHeartbeatTimestamp(Timestamp.valueOf(String.valueOf(Instant.now().getEpochSecond())));
        computeRepository.save(node);
        return true;
    }

    public ComputeNodeObject buildComputeNodeObjectFromComputeNode(ComputeNode node) {
        ComputeNodeMetadata metadata = ComputeNodeMetadata.builder()
                .port(node.getPort())
                .hostname(node.getHostname())
                .region(node.getRegion())
                .id(node.getId())
                .state(node.getState())
                .build();
        ComputeNodeSpecs specs = ComputeNodeSpecs.builder()
                .memory(node.getMemory())
                .cpus(node.getCpus())
                .storage(node.getStorage())
                .build();
        return ComputeNodeObject.builder()
                .specs(specs)
                .metadata(metadata)
                .build();
    }

    public ComputeNodeObject findNodeForVirtualMachine(VirtualMachineRequest virtualMachineRequest) {
        ComputeNode host = computeRepository.findOneByVirtualMachineSpecsAndHeartbeatTimeThreshold(60);
        if (host != null) {
           return buildComputeNodeObjectFromComputeNode(host);
        }
        return null;
    }

    public void updateNodeResourcesData(UUID id, VirtualMachineSpecs specs, int sign) {
        ComputeNode node = computeRepository.getComputeNodeById(id);
        node.setCpus(node.getCpus() + specs.getCpus() * sign);
        node.setMemory(node.getMemory() + specs.getMemory() * sign);
        node.setStorage(node.getStorage() + specs.getDisk() * sign);
        computeRepository.save(node);
    }

    public ComputeNode getComputeNodeById(UUID id) {
        return computeRepository.getComputeNodeById(id);
    }

}
