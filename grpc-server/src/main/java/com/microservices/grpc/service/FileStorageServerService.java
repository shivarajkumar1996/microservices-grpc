package com.microservices.grpc.service;

import com.microservices.grpc.File;
import com.microservices.grpc.FileStorageServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class FileStorageServerService extends FileStorageServiceGrpc.FileStorageServiceImplBase {

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

    @Override
    public void getFile(File file, StreamObserver<File> responseObserver) {
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
