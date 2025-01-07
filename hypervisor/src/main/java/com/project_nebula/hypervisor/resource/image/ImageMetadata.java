package com.project_nebula.hypervisor.resource.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageMetadata {
    private String url;
    private ImageSource source;
}
