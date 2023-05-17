package com.microservices.grpc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.grpc.*;
import com.microservices.grpc.constants.CommonConstants;
import com.microservices.grpc.exceptions.ErrorCode;
import com.microservices.grpc.exceptions.FileAlreadyExistsException;
import com.microservices.grpc.exceptions.ResourceNotFoundException;
import com.microservices.grpc.pojo.UserPojo;
import com.microservices.grpc.util.FileUtility;
import com.microservices.grpc.util.CommonUtility;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;


@GrpcService
@Log4j2
public class UserServerService extends UserServiceGrpc.UserServiceImplBase {



    @Autowired
    FileUtility fileUtil;

    @Autowired
    CommonUtility converterUtil;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
       /*
           - Logic is to store all the files with id as name with its respective extension
           - Whenever a get requests is received by the server, search for the file name with id
           - If it exists, parse the csv/xml into a pojo
           - If not, throw ResourceNotFoundException
           - Convert the POJO to protobuf format
           - Send the response
        */
        String id = String.valueOf(request.getId());
        UserPojo userPojo = fileUtil.findById(id);
        if(userPojo == null){
            throw new ResourceNotFoundException("Resource not found.",  Map.of("id", id, "message", "Resource Not Found"));
        }
        User responseUser = null;
        try {
            responseUser = converterUtil.convertToProtoBuf(userPojo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        responseObserver.onNext(responseUser);
        responseObserver.onCompleted();
    }


    /*  -	Assuming the request body is validated for the correctness of the input parameters, check if there is already an existing file with the given id present in either CSV/XML
        -	If present, throw error saying already exists
        -	If not, create the CSV/XML and send the success response saying created, with a proper success response as below
            -   {id: 1, path: /grpc/users/<id>, statusMessage: User created successfully}
*/
    @Override
    public void createUser(CreateOrSaveUserRequest createOrSaveUserRequest, StreamObserver<CreateOrSaveUserResponse> responseObserver) {
        User userRequest = createOrSaveUserRequest.getUser();
        String fileType = createOrSaveUserRequest.getFileType();
        if(fileUtil.getFileNameForId(String.valueOf(userRequest.getId())) != null){
            throw new FileAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS.getMessage(),  Map.of("id", String.valueOf(userRequest.getId()), "message", ErrorCode.USER_ALREADY_EXISTS.getMessage()));
        }
        UserPojo userPojo;
        CreateOrSaveUserResponse createOrSaveUserResponse = null;
        try {
            userPojo = converterUtil.convertToPojo(userRequest);
            if(fileUtil.create(userPojo, fileType)){
                createOrSaveUserResponse = CreateOrSaveUserResponse.newBuilder().setId(userPojo.getId()).setStatusMessage(CommonConstants.SUCCESSFUL_POST_MESSAGE).setPath(String.format("/grpc/users/%s", userPojo.getId())).build();
            }

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        responseObserver.onNext(createOrSaveUserResponse);
        responseObserver.onCompleted();
    }


    /*  -	Assuming the request body is validated for the correctness of the input parameters, check if there is already an existing file with the given id present in either CSV/XML
       -	If present, update the existing file with new details
       -	If not, create the CSV/XML
       -    Send the success response as below
           -   {id: 1, path: /grpc/users/<id>, statusMessage: User updated successfully}
*/
    @Override
    public void saveUser(CreateOrSaveUserRequest createOrSaveUserRequest, StreamObserver<CreateOrSaveUserResponse> responseObserver) {
        User userRequest = createOrSaveUserRequest.getUser();
        UserPojo userPojo;
        CreateOrSaveUserResponse createOrSaveUserResponse = null;

        try {
            userPojo = converterUtil.convertToPojo(userRequest);
            if(fileUtil.save(userPojo)){
                createOrSaveUserResponse = CreateOrSaveUserResponse.newBuilder().setId(userPojo.getId()).setStatusMessage(CommonConstants.SUCCESSFUL_PUT_MESSAGE).setPath(String.format("/grpc/users/%s", userPojo.getId())).build();
            }
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        responseObserver.onNext(createOrSaveUserResponse);
        responseObserver.onCompleted();
    }

}
