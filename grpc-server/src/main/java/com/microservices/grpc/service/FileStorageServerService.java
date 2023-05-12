package com.microservices.grpc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.grpc.CreateOrSaveFileRequest;
import com.microservices.grpc.File;
import com.microservices.grpc.FileStorageServiceGrpc;
import com.microservices.grpc.RequestId;
import com.microservices.grpc.exceptions.ErrorCode;
import com.microservices.grpc.exceptions.FileAlreadyExistsException;
import com.microservices.grpc.exceptions.ResourceNotFoundException;
import com.microservices.grpc.pojo.FilePojo;
import com.microservices.grpc.util.FileUtility;
import com.microservices.grpc.utility.Utility;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;


@GrpcService
@Log4j2
public class FileStorageServerService extends FileStorageServiceGrpc.FileStorageServiceImplBase {



    @Autowired
    FileUtility fileUtil;

    @Autowired
    Utility converterUtil;

    @Override
    public void getFile(RequestId request, StreamObserver<File> responseObserver) {
       /*
           - Logic is to store all the files with id as name with its respective extension
           - Whenever a get requests is received by the server, search for the file name with id
           - If it exists, parse the csv/xml into a pojo
           - If not, throw ResourceNotFoundException
           - Convert the POJO to protobuf format
           - Send the response
        */
        String id = String.valueOf(request.getId());
        FilePojo filePojo = fileUtil.findById(id);
        if(filePojo == null){
            throw new ResourceNotFoundException("Resource not found.",  Map.of("id", id, "message", "Resource Not Found"));
        }
        File responseFile = null;
        try {
            responseFile = converterUtil.convertToProtoBuf(filePojo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        responseObserver.onNext(responseFile);
        responseObserver.onCompleted();
    }


    /*  -	Assuming the request body is validated for the correctness of the input parameters, check if there is already an existing file with the given id present in either CSV/XML
        -	If present, throw error saying already exists
        -	If not, create the CSV/XML and send the success response saying created, with a proper success response as below
            -   {id: 1, path: /users/I, status: Created, code: 200}
*/
    @Override
    public void createFile(CreateOrSaveFileRequest request, StreamObserver<File> responseObserver) {
        File file = request.getFile();
        String fileType = request.getFileType();
        if(fileUtil.doesFileExist(String.valueOf(file.getId()))){
            throw new FileAlreadyExistsException(ErrorCode.FILE_ALREADY_EXISTS.getMessage(),  Map.of("id", String.valueOf(file.getId()), "message", ErrorCode.FILE_ALREADY_EXISTS.getMessage()));
        }
        FilePojo filePojo = null;
        File responseFile = null;
        try {
            filePojo = converterUtil.convertToPojo(file);
            responseFile = converterUtil.convertToProtoBuf(fileUtil.create(filePojo, fileType));
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        responseObserver.onNext(responseFile);
        responseObserver.onCompleted();
    }

    @Override
    public void updateFile(File file, StreamObserver<File> responseObserver) {
        int id = file.getId();
        String name = file.getName();
        String dob = file.getDob();
        double salary = file.getSalary();
        int age = file.getAge();

        File responseFile = File.newBuilder()
                .setId(id)
                .setName(name)
                .setDob(dob)
                .setSalary(salary)
                .setAge(age)
                .build();


        responseObserver.onNext(responseFile);
        responseObserver.onCompleted();
    }

}
