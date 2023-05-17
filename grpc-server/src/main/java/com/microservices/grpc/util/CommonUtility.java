package com.microservices.grpc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.microservices.grpc.CreateOrSaveUserResponse;
import com.microservices.grpc.User;
import com.microservices.grpc.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CommonUtility {

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


}
