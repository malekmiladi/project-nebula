package com.project_nebula.compute_node.resource.image;

import lombok.Setter;
import org.libvirt.StorageVol;
import org.libvirt.Stream;

@Setter
public class ImageProvider {

    private ImageDownloadStrategy strategy;

    public ImageProvider(ImageDownloadStrategy strategy) {
        this.strategy = strategy;
    }

    public void writeImageToVolume(String url, Stream destination, StorageVol volume) throws Exception {
        strategy.getImage(url, destination, volume);
    }

}
