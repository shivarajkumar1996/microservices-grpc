package com.microservices.grpc.service;

import com.microservices.grpc.*;
import com.microservices.grpc.mapper.ServiceExceptionMapper;
import com.microservices.grpc.pojo.Response;
import com.microservices.grpc.pojo.UserPojo;
import com.microservices.grpc.util.CommonUtility;
import io.grpc.StatusRuntimeException;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class UserClientService {

    @GrpcClient("grpc-service")
    UserServiceGrpc.UserServiceBlockingStub synchronousClient;
    @Autowired
    CommonUtility convertUtil;


    public UserPojo getUser(int id) throws IOException {
        log.info("Processing GET request for user id: {}", id);
        GetUserRequest getUserRequest = GetUserRequest.newBuilder().setId(id).build();
        User responseUser;
        try {
            responseUser = synchronousClient.getUser(getUserRequest);
        } catch (StatusRuntimeException error) {
            log.error("Error while getting user details, reason {} ", error.getMessage());
            throw ServiceExceptionMapper.map(error);
        }
        log.debug("Got response: {}", responseUser.toString());
        return convertUtil.convertToPojo(responseUser);

    }

    public Response createUser(UserPojo userPojo, String fileType) throws IOException {
        User user = convertUtil.convertToProtoBuf(userPojo);
        CreateOrSaveUserRequest createOrSaveUserRequest = CreateOrSaveUserRequest.newBuilder().setUser(user).setFileType(fileType).build();
        CreateOrSaveUserResponse createOrSaveUserResponse ;
        try {
            createOrSaveUserResponse = synchronousClient.createUser(createOrSaveUserRequest);
        } catch (StatusRuntimeException error) {
            log.error("Error while creating user, reason {} ", error.getMessage());
            throw ServiceExceptionMapper.map(error);
        }
        log.info("Successfully created new user. id: {}", createOrSaveUserResponse.getId());
        return convertUtil.getResponse(createOrSaveUserResponse);

    }

    public Response saveUser(UserPojo userPojo) throws IOException {
        log.info("Processing request for user id: {}", userPojo.getId());
        User user = convertUtil.convertToProtoBuf(userPojo);
        CreateOrSaveUserRequest createOrSaveUserRequest = CreateOrSaveUserRequest.newBuilder().setUser(user).build();
        CreateOrSaveUserResponse createOrSaveUserResponse;
        try {
            createOrSaveUserResponse = synchronousClient.saveUser(createOrSaveUserRequest);
        } catch (StatusRuntimeException error) {
            log.error("Error while saving user, reason {} ", error.getMessage());
            throw ServiceExceptionMapper.map(error);
        }
        log.info("Successfully saved user. id: {}", createOrSaveUserResponse.getId());
        return convertUtil.getResponse(createOrSaveUserResponse);

    }


}
