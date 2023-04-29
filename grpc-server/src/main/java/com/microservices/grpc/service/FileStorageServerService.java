package com.microservices.grpc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.grpc.File;
import com.microservices.grpc.FileStorageServiceGrpc;
import com.microservices.grpc.RequestId;
import com.microservices.grpc.pojo.FilePojo;
import com.microservices.grpc.util.FileUtility;
import com.microservices.grpc.utility.Utility;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;


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
        FilePojo filePojo = fileUtil.findById(String.valueOf(request.getId()));
        File responseFile = null;
        try {
            responseFile = converterUtil.convertToProtoBuf(filePojo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        responseObserver.onNext(responseFile);
        responseObserver.onCompleted();
    }

    @Override
    public void createFile(File file, StreamObserver<File> responseObserver) {
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
