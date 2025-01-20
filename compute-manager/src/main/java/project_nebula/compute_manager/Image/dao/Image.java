package project_nebula.compute_manager.Image.dao;

import com.project_nebula.shared.resource.image.Source;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Source source;
    private String name;
    private String architecture;
    private String version;
    private String distribution;

    private String url;

}
