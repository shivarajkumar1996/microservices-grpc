package com.microservices.grpc.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.microservices.grpc.File;
import com.microservices.grpc.pojo.FilePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Utility {

    @Autowired
    public Gson customGsonBuilder;
    public File convertToProtoBuf(FilePojo filePojo) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(filePojo);
        return customGsonBuilder.fromJson(jsonString, File.class);
    }

    public FilePojo convertToPojo(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = customGsonBuilder.toJson(file);
        return mapper.readValue(jsonString, FilePojo.class);
    }
}
