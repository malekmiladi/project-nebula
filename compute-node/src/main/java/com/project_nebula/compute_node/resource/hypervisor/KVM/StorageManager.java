package com.project_nebula.compute_node.resource.hypervisor.KVM;

import com.project_nebula.compute_node.ComputeConfiguration;
import com.project_nebula.compute_node.utils.ApplicationControl;
import com.project_nebula.compute_node.utils.XMLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

@Slf4j
public class StorageManager {

    private final StoragePool storagePool;

    StorageManager(Connect hypervisorConn) {
        storagePool = fetchDefaultStoragePool(hypervisorConn);
    }

    public StoragePool fetchDefaultStoragePool(Connect hypervisorConn) {
        try {
            return hypervisorConn.storagePoolLookupByName(ComputeConfiguration.STORAGE_POOL_NAME);
        } catch (LibvirtException exception) {
            log.warn("Could not find storage pool '{}': {}", ComputeConfiguration.STORAGE_POOL_NAME, exception.getMessage());
            log.info("Creating storage pool '{}'", ComputeConfiguration.STORAGE_POOL_NAME);
            return createDefaultStoragePool(hypervisorConn);
        }
    }

    private StoragePool createDefaultStoragePool(Connect hypervisorConn) {
        String xmlDescription = "";
        try {
            XMLBuilder builder = new XMLBuilder("pool");
            xmlDescription = builder
                    .setAttribute("type", "dir")
                    .addChild("name")
                    .setText(ComputeConfiguration.STORAGE_POOL_NAME)
                    .stepBack(1)
                    .addChild("target")
                    .addChild("path")
                    .setText("/var/lib/libvirt/images")
                    .build();
        } catch (Exception exception) {
            log.error("Failed to define storage '{}' pool XML description: {}", ComputeConfiguration.STORAGE_POOL_NAME, exception.getMessage());
            ApplicationControl.exit(1);
        }

        try {
            StoragePool storagePool = hypervisorConn.storagePoolDefineXML(xmlDescription, 0);
            storagePool.setAutostart(1);
            storagePool.create(0);
            return storagePool;
        } catch (LibvirtException exception) {
            log.error("Failed to create storage pool '{}': {}", ComputeConfiguration.STORAGE_POOL_NAME, exception.getMessage());
            ApplicationControl.exit(1);
        }
        return null;
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

}
