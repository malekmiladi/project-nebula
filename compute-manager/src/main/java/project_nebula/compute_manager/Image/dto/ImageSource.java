package project_nebula.compute_manager.Image.dto;

import com.project_nebula.shared.resource.image.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageSource {
    private Source source;
    private String url;
}
