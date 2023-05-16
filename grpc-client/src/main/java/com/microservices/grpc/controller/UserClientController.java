package com.microservices.grpc.controller;

import com.microservices.grpc.exceptions.ErrorCode;
import com.microservices.grpc.exceptions.InvalidArgumentException;
import com.microservices.grpc.pojo.Response;
import com.microservices.grpc.pojo.UserPojo;
import com.microservices.grpc.service.UserClientService;
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
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/grpc")
@AllArgsConstructor
@Log4j2
@Validated
public class UserClientController {

    @Autowired
    UserClientService userClientService;

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") @NotNull @Positive(message = "User id must be greater than 0") int id) throws IOException {
        log.info("Received request for user with id: {}", id);
        return ResponseEntity.ok(userClientService.getUser(id));
    }

    @PostMapping(value = "/users/", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> create(@RequestHeader(value = "fileType", defaultValue = "CSV") String fileType, @Valid @RequestBody() UserPojo userPojo) throws IOException {
        log.info("Received PUT request for user with id: {}", userPojo.getId());
        log.debug("Json String: {}", userPojo.toString());
        fileType = fileType.equalsIgnoreCase("XML") ? fileType.toLowerCase() : "csv";
        Response response = userClientService.createUser(userPojo, fileType);
        return ResponseEntity.created(URI.create(String.format("/grpc/users/%s", response.getId()))).body(response);
    }

    @PutMapping(value = "/users/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> save(@PathVariable @NotNull @Positive(message = "User id must be greater than 0") int id, @Valid @RequestBody() UserPojo userPojo) throws IOException {
        log.info("Received PUT request for user with id: {}", id);
        log.debug("Json String: {}", userPojo.toString());
        if (id != userPojo.getId()) {
            throw new InvalidArgumentException(ErrorCode.BAD_ARGUMENT.getMessage(), Map.of("parameter", "id", "message", "Value of variable: id must be same in both path param and request body."));
        }

        Response response = userClientService.saveUser(userPojo);
        return ResponseEntity.ok(response);
    }
}
