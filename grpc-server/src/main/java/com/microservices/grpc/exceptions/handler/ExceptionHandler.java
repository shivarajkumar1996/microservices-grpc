package com.microservices.grpc.exceptions.handler;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.microservices.grpc.ErrorDetail;
import com.microservices.grpc.exceptions.ErrorCode;
import com.microservices.grpc.exceptions.FileStorageException;
import com.microservices.grpc.exceptions.ResourceNotFoundException;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class ExceptionHandler {

    @GrpcExceptionHandler(ResourceNotFoundException.class)
    public StatusRuntimeException handleResourceNotFoundException(ResourceNotFoundException error) {
        var errorMetaData = error.getErrorMetaData();
        var errorInfo =
                ErrorDetail.newBuilder()
                        .setErrorCode(ErrorCode.RESOURCE_NOT_FOUND.getShortCode())
                        .setMessage(error.getMessage())
                        .putAllMetadata(errorMetaData)
                        .build();
        var status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Code.NOT_FOUND.getNumber())
                        .setMessage(ErrorCode.RESOURCE_NOT_FOUND.getMessage())
                        .addDetails(Any.pack(errorInfo))
                        .build();
        return StatusProto.toStatusRuntimeException(status);
    }

    @GrpcExceptionHandler(FileStorageException.class)
    public StatusRuntimeException handleFileStorageException(FileStorageException error) {
        var errorMetaData = error.getErrorMetaData();
        var errorInfo =
                ErrorDetail.newBuilder()
                        .setErrorCode(ErrorCode.FILE_STORAGE_ERROR.getShortCode())
                        .setMessage(error.getMessage())
                        .putAllMetadata(errorMetaData)
                        .build();
        var status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Code.INTERNAL.getNumber())
                        .setMessage(ErrorCode.FILE_STORAGE_ERROR.getMessage())
                        .addDetails(Any.pack(errorInfo))
                        .build();
        return StatusProto.toStatusRuntimeException(status);
    }
}