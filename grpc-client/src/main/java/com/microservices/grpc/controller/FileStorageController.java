package com.microservices.grpc.controller;

import com.google.protobuf.Descriptors;
import com.microservices.grpc.service.FileStorageClientService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@Log4j2
public class FileStorageController {

    @Autowired
    FileStorageClientService fileStorageClientService;

    @GetMapping("/grpc/users/{id}")
    public Map<Descriptors.FieldDescriptor, Object> getFile(@PathVariable("id") String id){
        log.info("Received request for file with id: {}", id);
        return fileStorageClientService.getFile(Integer.parseInt(id));
    }
}
