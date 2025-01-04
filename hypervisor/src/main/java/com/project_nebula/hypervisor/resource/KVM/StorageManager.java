package com.project_nebula.hypervisor.resource.KVM;

import com.project_nebula.hypervisor.resource.image.*;
import com.project_nebula.hypervisor.utils.XMLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

@Slf4j
public class StorageManager {

    private final StoragePool storagePool;

    StorageManager(Connect hypervisorConn, String defaultStoragePoolName) {
        storagePool = fetchDefaultStoragePool(hypervisorConn, defaultStoragePoolName);
    }

    public StoragePool fetchDefaultStoragePool(Connect hypervisorConn, String defaultStoragePoolName) {
        try {
            return hypervisorConn.storagePoolLookupByName(defaultStoragePoolName);
        } catch (LibvirtException exception) {
            log.warn("Could not find storage pool '{}': {}", defaultStoragePoolName, exception.getMessage());
            log.info("Creating storage pool '{}'", defaultStoragePoolName);
            return createDefaultStoragePool(hypervisorConn, defaultStoragePoolName);
        }
    }

    private StoragePool createDefaultStoragePool(Connect hypervisorConn, String defaultStoragePoolName) {
        String xmlDescription = "";
        try {
            XMLBuilder builder = new XMLBuilder("pool");
            xmlDescription = builder
                    .setAttribute("type", "dir")
                    .addChild("name")
                    .setText(defaultStoragePoolName)
                    .stepBack(1)
                    .addChild("target")
                    .addChild("path")
                    .setText("/var/lib/libvirt/images")
                    .build();
        } catch (Exception exception) {
            log.error("Failed to define storage '{}' pool XML description: {}", defaultStoragePoolName, exception.getMessage());
            return null;
        }

        try {
            StoragePool storagePool = hypervisorConn.storagePoolDefineXML(xmlDescription, 0);
            storagePool.setAutostart(1);
            storagePool.create(0);
            return storagePool;
        } catch (LibvirtException exception) {
            log.error("Failed to create storage pool '{}': {}", defaultStoragePoolName, exception.getMessage());
            return null;
        }
    }

    private String createVolumeXMLDescription(String id, int size) throws ParserConfigurationException, TransformerException, NullPointerException {
        XMLBuilder builder = new XMLBuilder("volume");
        return builder
                .setAttribute("type", "file")
                .addChild("name")
                .setText(id + ".qcow2")
                .stepBack(1)
                .addChild("capacity")
                .setAttribute("unit", "G")
                .setText(String.valueOf(size))
                .stepBack(1)
                .addChild("allocation")
                .setAttribute("unit", "G")
                .setText(String.valueOf(size))
                .stepBack(1)
                .addChild("target")
                .addChild("format")
                .setAttribute("type", "qcow2")
                .build();
    }

    public StorageVol createVolume(String id, int size) throws ParserConfigurationException, TransformerException, LibvirtException {
        String xmlDescription = createVolumeXMLDescription(id, size);
        return storagePool.storageVolCreateXML(xmlDescription, 0);
    }

    public void deleteVolume(String id) throws LibvirtException {
        StorageVol volume = storagePool.storageVolLookupByName(id);
        volume.delete(0);
    }

    public void uploadImageToVolume(ImageSource source, String url, Stream fileStream, StorageVol volume) throws Exception {
        ImageDownloadStrategy downloadStrategy = source == ImageSource.LOCAL ?
                new LocalImageStrategy() :
                new OnlineImageStrategy();
        ImageProvider imageProvider = new ImageProvider(downloadStrategy);
        imageProvider.writeImageToVolume(url, fileStream, volume);
    }

}
