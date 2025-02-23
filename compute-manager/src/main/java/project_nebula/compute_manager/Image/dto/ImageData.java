package project_nebula.compute_manager.Image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageData {
    private UUID id;
    private ImageMetadata metadata;
    private ImageSource source;
}
