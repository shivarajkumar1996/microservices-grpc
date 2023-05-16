package com.microservices.grpc.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.microservices.grpc.CreateOrSaveUserResponse;
import com.microservices.grpc.User;
import com.microservices.grpc.pojo.Response;
import com.microservices.grpc.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Utility {

    @Autowired
    public Gson customGsonBuilder;
    public User convertToProtoBuf(UserPojo userPojo) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(userPojo);
        return customGsonBuilder.fromJson(jsonString, User.class);
    }

    public UserPojo convertToPojo(User user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = customGsonBuilder.toJson(user);
        return mapper.readValue(jsonString, UserPojo.class);
    }

    public Response getResponse(CreateOrSaveUserResponse createOrSaveUserResponse){
        Response response = new Response();
        response.setId(createOrSaveUserResponse.getId());
        response.setStatusMessage(createOrSaveUserResponse.getStatusMessage());
        response.setPath(createOrSaveUserResponse.getPath());
        return response;
    }

}
