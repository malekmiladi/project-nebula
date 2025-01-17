package com.project_nebula.shared.resource.image;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Data
public class ImageMetadata {
    private final String url;
    private final Source source;
}
