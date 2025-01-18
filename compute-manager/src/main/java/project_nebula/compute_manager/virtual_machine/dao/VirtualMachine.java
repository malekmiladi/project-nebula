package project_nebula.compute_manager.virtual_machine.dao;

import com.project_nebula.shared.resource.VirtualMachineState;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import project_nebula.compute_manager.Image.dao.Image;
import project_nebula.compute_manager.project.dao.Project;

import java.sql.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @CreatedDate
    private Date createdAt;

    private String region;
    private int cpus;
    private int memory;
    private int disk;

    private String name;
    private String description;
    private String internalIpV4;
    private String internalIpV6;
    private String externalIpV4;
    private String externalIpV6;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    private VirtualMachineState state;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}

