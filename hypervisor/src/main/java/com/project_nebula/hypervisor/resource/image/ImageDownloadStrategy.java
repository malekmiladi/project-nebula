package com.project_nebula.hypervisor.resource.image;

import org.libvirt.StorageVol;
import org.libvirt.Stream;

public interface ImageDownloadStrategy {
    void getImage(String url, Stream destination, StorageVol storage) throws Exception;
}
