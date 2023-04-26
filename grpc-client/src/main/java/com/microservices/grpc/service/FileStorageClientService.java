package com.microservices.grpc.service;

import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import com.microservices.grpc.File;
import com.microservices.grpc.FileStorageServiceGrpc;
import com.microservices.grpc.pojo.FilePojo;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
public class FileStorageClientService {

    @GrpcClient("grpc-service")
    FileStorageServiceGrpc.FileStorageServiceBlockingStub synchronousClient;

    @Autowired
    Gson customGsonBuilder;


    public Map<Descriptors.FieldDescriptor, Object> getFile(int id) {
        log.info("Processing request for id: {}", id);

        File fileRequest = File.newBuilder().setAge(27)
                .setSalary(100000)
                .setDob("16-07-1996")
                .setName("Shiva")
                .setId(id)
                .build();
        File responseFile = synchronousClient.getFile(fileRequest);
        log.info("Got Response id: {}", responseFile.getId());
        return responseFile.getAllFields();

    }

    public File createFile(String filePojoString) {
        File fileRequest = customGsonBuilder.fromJson(filePojoString, File.class);
        File responseFile = synchronousClient.getFile(fileRequest);
        log.info("Got Response : {}", responseFile);
        return responseFile;

    }

    public File updateFile(FilePojo filePojo) {
        log.info("Processing request for id: {}", filePojo.getId());
        File fileRequest = File.newBuilder().setAge(27)
                .setSalary(100000)
                .setDob("16-07-1996")
                .setName("Shiva")
                .setId(filePojo.getId())
                .build();
        File responseFile = synchronousClient.getFile(fileRequest);
        log.info("Got Response id: {}", responseFile.getId());
        return responseFile;

    }


}
