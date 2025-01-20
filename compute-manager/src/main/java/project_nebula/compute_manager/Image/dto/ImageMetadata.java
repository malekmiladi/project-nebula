package project_nebula.compute_manager.Image.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageMetadata {
    private String name;
    private String distribution;
    private String architecture;
    private String version;
}
