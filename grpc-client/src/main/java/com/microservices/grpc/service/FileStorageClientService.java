package com.microservices.grpc.service;

import com.google.protobuf.Descriptors;
import com.microservices.grpc.File;
import com.microservices.grpc.FileStorageServiceGrpc;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Service
@Log4j2
public class FileStorageClientService {

    @GrpcClient("grpc-service")
    FileStorageServiceGrpc.FileStorageServiceBlockingStub synchronousClient;

    public Map<Descriptors.FieldDescriptor, Object> getFile(int id){
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
}
