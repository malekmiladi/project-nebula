package project_nebula.compute_manager.virtual_machine;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project_nebula.compute_manager.virtual_machine.dto.VirtualMachineData;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project/{userId}/{projectId}/virtual-machine")
public class VirtualMachineController {

    private final VirtualMachineService virtualMachineService;

    @PostMapping
    public ResponseEntity<VirtualMachineData> createVirtualMachine(@PathVariable UUID userId, @PathVariable UUID projectId, @RequestBody VirtualMachineData virtualMachineData) {

    }

}
