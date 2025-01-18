package project_nebula.compute_manager.virtual_machine;

import org.springframework.data.jpa.repository.JpaRepository;
import project_nebula.compute_manager.virtual_machine.dao.VirtualMachine;

import java.util.UUID;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, UUID> {
}
