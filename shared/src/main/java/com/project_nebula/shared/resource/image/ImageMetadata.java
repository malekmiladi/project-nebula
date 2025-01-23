package com.project_nebula.shared.resource.image;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ImageMetadata {
    private String url;
    private Source source;
}
