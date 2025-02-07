package com.project_nebula.hypervisor.resource.kvm;

import lombok.extern.slf4j.Slf4j;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.libvirt.Stream;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class ConnectionProvider implements AutoCloseable {

    private static ConnectionProvider instance;
    private Connect HYPERVISOR_CONNECTION;

    private final String uri;
    private boolean isConnected = false;
    private final ScheduledExecutorService healthCheckWorker = Executors.newSingleThreadScheduledExecutor();


    private ConnectionProvider(String hyperVisorConnectionUri) throws Exception {
        uri = hyperVisorConnectionUri;
        HYPERVISOR_CONNECTION = connectToHypervisor(uri);
        startHealthCheckThread();
    }

    public static ConnectionProvider getInstance(String hyperVisorConnectionUri) throws Exception {
        if (instance == null || !instance.getConnection().getURI().equals(hyperVisorConnectionUri)) {
            instance = new ConnectionProvider(hyperVisorConnectionUri);
        }
        return instance;
    }

    private Connect connectToHypervisor(String hypervisorUri) throws Exception {
        try {
            Connect conn = new Connect(hypervisorUri);
            isConnected = true;
            log.info("Connected to Hypervisor at \"{}\"", hypervisorUri);
            return conn;
        } catch (LibvirtException exception) {
            log.error("Failed to connect to hypervisor at \"{}\": {}", hypervisorUri, exception.getMessage());
            isConnected = false;
            throw new Exception("Failed to connect to hypervisor. Please verify that your system supports KVM virtualization.");
        }
    }

    public Connect getConnection() {
        if (!isConnected) {
            throw new RuntimeException("Service is unreachable. Hypervisor unreachable.");
        }
        return HYPERVISOR_CONNECTION;
    }

    public Stream getNewStream() throws Exception {
        return HYPERVISOR_CONNECTION.streamNew(Stream.VIR_STREAM_NONBLOCK);
    }

    private void startHealthCheckThread() {
        healthCheckWorker.scheduleAtFixedRate(() -> {
            if (!isConnected) {
                try {
                    HYPERVISOR_CONNECTION = connectToHypervisor(uri);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        try {
            HYPERVISOR_CONNECTION.close();
        } catch (Exception e) {
            log.error("Failed to close connection to hypervisor: {}", e.getMessage());
        } finally {
            healthCheckWorker.shutdown();
        }
    }

}
