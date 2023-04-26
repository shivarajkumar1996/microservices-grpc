package com.microservices.grpc.exceptions;

import java.util.Map;

public class FileStorageException extends BaseException {
    public FileStorageException(String message, Map<String, String> errorMetaData) {
        this(ErrorCode.FILE_STORAGE_ERROR,message, errorMetaData);
    }

    public FileStorageException(ErrorCode errorCode, String message, Map<String, String> errorMetaData) {
        super(errorCode,message, errorMetaData);
    }

    public FileStorageException(String message, Map<String, String> errorMetaData, Throwable cause) {
        this(ErrorCode.FILE_STORAGE_ERROR,message, errorMetaData, cause);
    }

    public FileStorageException(ErrorCode errorCode,
                                String message, Map<String, String> errorMetaData, Throwable cause) {
        super(errorCode,message, errorMetaData, cause);
    }
}
