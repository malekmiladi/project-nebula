package com.project_nebula.hypervisor.resource.image;

import lombok.NoArgsConstructor;
import org.libvirt.LibvirtException;
import org.libvirt.StorageVol;
import org.libvirt.Stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@NoArgsConstructor
public class LocalImageStrategy implements ImageDownloadStrategy {

    private final int CHUNK_SIZE = 4096;

    @Override
    public void getImage(String url, Stream destination, StorageVol volume) throws URISyntaxException, MalformedURLException, IOException, LibvirtException {
        File imageFile = new File(url);
        volume.upload(destination, 0, imageFile.length(), 0);
        FileInputStream in = new FileInputStream(imageFile);
        byte[] buffer = new byte[CHUNK_SIZE];
        while (in.read(buffer, 0, CHUNK_SIZE) != -1) {
            destination.send(buffer);
        }
        destination.finish();
        destination.close();
        in.close();
    }
}
