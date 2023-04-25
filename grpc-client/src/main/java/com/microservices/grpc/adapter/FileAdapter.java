package com.microservices.grpc.adapter;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.util.JsonFormat;
import com.microservices.grpc.File;

import java.io.IOException;

public class FileAdapter extends TypeAdapter<File> {
    /**
     * Override the read method to return a {@File} object from it's json representation.
     */
    @Override
    public File read(JsonReader jsonReader) throws IOException {
        // Create a builder for the File message
        File.Builder fileBuilder = File.newBuilder();
        // Use the JsonFormat class to parse the json string into the builder object
        // The Json string will be parsed fromm the JsonReader object
        JsonParser jsonParser = new JsonParser();
        JsonFormat.parser().merge(jsonParser.parse(jsonReader).toString(), fileBuilder);
        // Return the built File message
        return fileBuilder.build();
    }
    /**
     * Override the write method and set the json value of the File message.
     */
    @Override
    public void write(JsonWriter jsonWriter, File file) throws IOException {
        // Call the printer of the JsonFormat class to convert the File proto message to Json
        jsonWriter.jsonValue(JsonFormat.printer().print(file));
    }
}