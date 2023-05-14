package com.microservices.grpc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.microservices.grpc.File;
import com.microservices.grpc.exceptions.ErrorCode;
import com.microservices.grpc.exceptions.InvalidArgumentException;
import com.microservices.grpc.exceptions.ResourceNotFoundException;
import com.microservices.grpc.pojo.FilePojo;
import com.microservices.grpc.service.FileStorageClientService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public FilePojo create(@RequestHeader(value = "fileType", defaultValue = "CSV") String fileType, @Valid @RequestBody() FilePojo filePojo) throws IOException {
        log.info("Received PUT request for user with id: {}", filePojo.getId());
        log.debug("Json String: {}", filePojo.toString());
        fileType = fileType.equalsIgnoreCase("XML") ? fileType.toLowerCase(): "csv";
        return fileStorageClientService.createFile(filePojo , fileType);
    }

    @PutMapping(value = "/users/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public FilePojo save(@PathVariable @NotNull @Positive(message = "User id must be greater than 0") int id, @Valid @RequestBody() FilePojo filePojo) throws IOException {
        log.info("Received PUT request for file with id: {}", id);
        log.debug("Json String: {}", filePojo.toString());
        if(id != filePojo.getId()){
            throw new InvalidArgumentException(ErrorCode.BAD_ARGUMENT.getMessage(),  Map.of("parameter", "id", "message", "Value of variable: id must be same in both path param and request body."));
        }

        return fileStorageClientService.saveFile(filePojo);
    }
}
