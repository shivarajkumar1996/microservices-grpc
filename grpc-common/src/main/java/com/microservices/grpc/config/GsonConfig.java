package com.microservices.grpc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microservices.grpc.User;
import com.microservices.grpc.adapter.UserAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {

    @Bean
    public Gson getCustomGsonBuilder(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(User.class, new UserAdapter()).create();
        return gson;
    }


}
