package com.microservices.grpc.exceptions;

import java.util.Map;

public class FileAlreadyExistsException extends BaseException {
    public FileAlreadyExistsException(String message, Map<String, String> errorMetaData) {
        this(ErrorCode.FILE_ALREADY_EXISTS,message, errorMetaData);
    }

    public FileAlreadyExistsException(ErrorCode errorCode, String message, Map<String, String> errorMetaData) {
        super(errorCode,message, errorMetaData);
    }

    public FileAlreadyExistsException(String message, Map<String, String> errorMetaData, Throwable cause) {
        this(ErrorCode.FILE_ALREADY_EXISTS,message, errorMetaData, cause);
    }

    public FileAlreadyExistsException(ErrorCode errorCode,
                                      String message, Map<String, String> errorMetaData, Throwable cause) {
        super(errorCode,message, errorMetaData, cause);
    }
}
