package project_nebula.compute_manager.virtual_machine;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineData;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project/{userId}/{projectId}/virtual-machine")
public class VirtualMachineController {

    private final VirtualMachineService virtualMachineService;

    @PostMapping
    public ResponseEntity<VirtualMachineData> createVirtualMachine(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @RequestBody VirtualMachineData virtualMachineData
    ) {
        try {
            return ResponseEntity.ok(virtualMachineService.createVirtualMachine(projectId, virtualMachineData));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<VirtualMachineData> changeVirtualMachineState(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @PathVariable UUID id,
        @RequestBody VirtualMachineData virtualMachineData
    ) {
        try {
            return ResponseEntity.ok(virtualMachineService.changeVirtualMachineState(projectId, id, virtualMachineData));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VirtualMachineData> deleteVirtualMachine(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @PathVariable UUID id,
        @RequestBody VirtualMachineData virtualMachineData
    ) {
        try {
            return ResponseEntity.ok(virtualMachineService.startDeleteVirtualMachineWorkflow(projectId, id));
        }
    }

}
