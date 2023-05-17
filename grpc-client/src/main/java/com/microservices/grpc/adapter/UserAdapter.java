package com.microservices.grpc.adapter;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.util.JsonFormat;
import com.microservices.grpc.User;

import java.io.IOException;

/**
 * Custom adapter used by gson builder to convert User object to JSON value and vice versa
 * */
public class UserAdapter extends TypeAdapter<User> {
    /**
     * Override the read method to return a {@User} object from it's json representation.
     */
    @Override
    public User read(JsonReader jsonReader) throws IOException {
        // Create a builder for the User message
        User.Builder userBuilder = User.newBuilder();
        // Use the JsonFormat class to parse the json string into the builder object
        // The Json string will be parsed fromm the JsonReader object
        JsonParser jsonParser = new JsonParser();
        JsonFormat.parser().merge(jsonParser.parse(jsonReader).toString(), userBuilder);
        // Return the built User message
        return userBuilder.build();
    }
    /**
     * Override the write method and set the json value of the User message.
     */
    @Override
    public void write(JsonWriter jsonWriter, User user) throws IOException {
        // Call the printer of the JsonFormat class to convert the User proto message to Json
        jsonWriter.jsonValue(JsonFormat.printer().print(user));
    }
}