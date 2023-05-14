package com.microservices.grpc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import com.microservices.grpc.CreateOrSaveFileRequest;
import com.microservices.grpc.File;
import com.microservices.grpc.FileStorageServiceGrpc;
import com.microservices.grpc.RequestId;
import com.microservices.grpc.mapper.ServiceExceptionMapper;
import com.microservices.grpc.pojo.FilePojo;
import com.microservices.grpc.utility.Utility;
import io.grpc.StatusRuntimeException;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Log4j2
public class FileStorageClientService {

    @GrpcClient("grpc-service")
    FileStorageServiceGrpc.FileStorageServiceBlockingStub synchronousClient;
    @Autowired
    Utility convertUtil;


    public FilePojo getFile(int id) throws IOException {
        log.info("Processing GET request for id: {}", id);
        RequestId requestId = RequestId.newBuilder().setId(id).build();
        File responseFile = null;
        try {
             responseFile = synchronousClient.getFile(requestId);
        } catch (StatusRuntimeException error) {
            log.error("Error while getting file, reason {} ", error.getMessage());
            throw ServiceExceptionMapper.map(error);
        }
        log.debug("Got Response id: {}", responseFile.toString());
        return convertUtil.convertToPojo(responseFile);

    }

    public FilePojo createFile(FilePojo filePojo, String fileType) throws IOException {
        File file = convertUtil.convertToProtoBuf(filePojo);
        CreateOrSaveFileRequest fileRequest = CreateOrSaveFileRequest.newBuilder().setFile(file).setFileType(fileType).build();
        File responseFile = null;
        try {
            responseFile = synchronousClient.createFile(fileRequest);
        } catch (StatusRuntimeException error) {
            log.error("Error while creating file, reason {} ", error.getMessage());
            throw ServiceExceptionMapper.map(error);
        }
        log.info("Successfully created new user file for user id: {}", responseFile.getId());
        return convertUtil.convertToPojo(responseFile);

    }

    public FilePojo saveFile(FilePojo filePojo) throws IOException {
        log.info("Processing request for id: {}", filePojo.getId());
        File file = convertUtil.convertToProtoBuf(filePojo);
        CreateOrSaveFileRequest fileRequest = CreateOrSaveFileRequest.newBuilder().setFile(file).build();
        File responseFile = null;
        try {
            responseFile = synchronousClient.updateFile(fileRequest);
        } catch (StatusRuntimeException error) {
            log.error("Error while saving file, reason {} ", error.getMessage());
            throw ServiceExceptionMapper.map(error);
        }
        log.info("Successfully saved user file for user id: {}", responseFile.getId());
        return convertUtil.convertToPojo(responseFile);

    }


}
