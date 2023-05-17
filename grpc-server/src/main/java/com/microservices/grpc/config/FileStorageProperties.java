package com.microservices.grpc.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is a configuration property class. You can configure the storageDir value in application.yaml
 * file under resources folder. Parameter: file.storageDir
 * */
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String storageDir;

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }
}