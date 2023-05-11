package com.microservices.grpc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.Descriptors;
import com.microservices.grpc.File;
import com.microservices.grpc.adapter.FileAdapter;
import com.microservices.grpc.pojo.FilePojo;
import com.microservices.grpc.service.FileStorageClientService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/grpc")
@AllArgsConstructor
@Log4j2
@Validated
public class FileStorageController {

    @Autowired
    FileStorageClientService fileStorageClientService;

    @Autowired
    Gson customGsonBuilder;

    @GetMapping("/users/{id}")
    public FilePojo get( @PathVariable("id") @NotNull @Positive(message = "User id must be greater than 0") int id) throws IOException {
        log.info("Received request for file with id: {}", id);
        return fileStorageClientService.getFile(id);
    }

    @PostMapping(value = "/users/",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> create(@Valid @RequestBody() FilePojo filePojo) throws JsonProcessingException {
        log.info("Json String: {}", filePojo.toString());
        return ResponseEntity.ok(customGsonBuilder.toJson(fileStorageClientService.createFile(filePojo)));
    }

    @PutMapping(value = "/users/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FilePojo> save(@PathVariable String id, @RequestBody() FilePojo filePojo) {
        log.info("Received PUT request for file with id: {}", id);
        return ResponseEntity.ok(filePojo);
    }
}
