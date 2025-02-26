package com.project_nebula.hypervisor.resource.kvm;

import com.project_nebula.hypervisor.resource.image.ImageDownloadStrategy;
import com.project_nebula.hypervisor.resource.image.ImageProvider;
import com.project_nebula.hypervisor.resource.image.LocalImageStrategy;
import com.project_nebula.hypervisor.resource.image.OnlineImageStrategy;
import com.project_nebula.hypervisor.utils.XMLBuilder;
import com.project_nebula.shared.resource.image.Source;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.text.MessageFormat;

@Slf4j
public class StorageManager {

    private final StoragePool storagePool;

    StorageManager(Connect hypervisorConn, String defaultStoragePoolName, String defaultStoragePoolLocation) throws Exception {
        storagePool = fetchDefaultStoragePool(hypervisorConn, defaultStoragePoolName, defaultStoragePoolLocation);
    }

    public StoragePool fetchDefaultStoragePool(Connect hypervisorConn, String defaultStoragePoolName, String defaultStoragePoolLocation) throws Exception {
        try {
            StoragePool defaultStoragePool = hypervisorConn.storagePoolLookupByName(defaultStoragePoolName);
            log.info("Found default storage pool: {}", defaultStoragePoolName);
            return defaultStoragePool;
        } catch (LibvirtException exception) {
            log.warn("Could not find storage pool '{}': {}", defaultStoragePoolName, exception.getMessage());
            log.info("Creating storage pool '{}'", defaultStoragePoolName);
            return createDefaultStoragePool(hypervisorConn, defaultStoragePoolName, defaultStoragePoolLocation);
        }
    }

    private StoragePool createDefaultStoragePool(Connect hypervisorConn, String defaultStoragePoolName, String defaultStoragePoolLocation) throws Exception {
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
                    .setText(defaultStoragePoolLocation)
                    .build();
        } catch (Exception exception) {
            throw new Exception(MessageFormat.format("Failed to create XML description for storage pool \"{0}\"", defaultStoragePoolName), exception);
        }

        try {
            StoragePool storagePool = hypervisorConn.storagePoolDefineXML(xmlDescription, 0);
            storagePool.setAutostart(1);
            storagePool.create(0);
            return storagePool;
        } catch (LibvirtException exception) {
            log.error("Failed to create storage pool '{}': {}", defaultStoragePoolName, exception.getMessage());
            throw new Exception(MessageFormat.format("Failed to create storage pool \"{0}\". Verify that the application has the right system permissions.", defaultStoragePoolName), exception);
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

    public StorageVol createVolume(String id, int size) throws Exception {
        try {
            String xmlDescription = createVolumeXMLDescription(id, size);
            return storagePool.storageVolCreateXML(xmlDescription, 0);
        } catch (Exception exception) {
            throw new Exception(MessageFormat.format("Failed to create new volume.\n{0}", exception.getMessage()), exception);
        }
    }

    public StorageVol getVolumeById(String id) throws Exception {
        try {
            return storagePool.storageVolLookupByName(id + ".qcow2");
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to get volume by id {0}", id), e);
        }
    }

    public void deleteVolume(String id) throws Exception {
        try {
            StorageVol volume = getVolumeById(id);
            volume.delete(0);
        } catch (LibvirtException exception) {
            throw new Exception(MessageFormat.format("Failed to delete volume \"{0}\".qcow2", id), exception);
        }
    }

    public void deleteVolume(StorageVol volume) throws Exception {
        try {
            volume.delete(0);
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to delete volume \"{0}\"", volume.getName()), e);
        }
    }

    public void uploadImageToVolume(Source source, String url, Stream fileStream, StorageVol volume) throws Exception {
        ImageDownloadStrategy downloadStrategy = source == Source.LOCAL ?
                new LocalImageStrategy() :
                new OnlineImageStrategy();
        ImageProvider imageProvider = new ImageProvider(downloadStrategy);
        try {
            imageProvider.writeImageToVolume(url, fileStream, volume);
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("Failed to upload image \"{0}\" to volume \"{1}\"", url, volume.getName()), e);
        }
    }

}
