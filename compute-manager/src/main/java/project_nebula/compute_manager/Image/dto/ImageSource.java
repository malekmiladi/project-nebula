package project_nebula.compute_manager.Image.dto;

import com.project_nebula.shared.resource.image.Source;
import lombok.*;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageSource {
    private Source source;
    private String url;
}
