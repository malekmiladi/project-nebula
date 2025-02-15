package com.project_nebula.hypervisor.resource.image;

import lombok.NoArgsConstructor;
import org.libvirt.LibvirtException;
import org.libvirt.StorageVol;
import org.libvirt.Stream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

@NoArgsConstructor
public class OnlineImageStrategy implements ImageDownloadStrategy {

    private final int CHUNK_SIZE = 4096;

    @Override
    public void getImage(String url, Stream destination, StorageVol volume) throws URISyntaxException, IOException, LibvirtException {
        URL uri = new URI(url).toURL();
        URLConnection conn = uri.openConnection();
        long fileSize = conn.getContentLengthLong();
        BufferedInputStream in = new BufferedInputStream(uri.openStream());
        volume.upload(destination, 0, fileSize, 0);
        byte[] buffer = new byte[CHUNK_SIZE];
        while (in.read(buffer, 0, CHUNK_SIZE) != -1) {
            destination.send(buffer);
        }
        destination.finish();
        in.close();
    }

}
