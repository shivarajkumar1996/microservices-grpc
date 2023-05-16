package com.microservices.grpc.exceptions;

import java.util.Map;

public class InvalidArgumentException extends BaseException {
    public InvalidArgumentException(String message, Map<String, String> errorMetaData) {
        this(ErrorCode.BAD_ARGUMENT,message, errorMetaData);
    }

    public InvalidArgumentException(ErrorCode errorCode, String message, Map<String, String> errorMetaData) {
        super(errorCode,message, errorMetaData);
    }

    public InvalidArgumentException(String message, Map<String, String> errorMetaData, Throwable cause) {
        this(ErrorCode.BAD_ARGUMENT,message, errorMetaData, cause);
    }

    public InvalidArgumentException(ErrorCode errorCode,
                                    String message, Map<String, String> errorMetaData, Throwable cause) {
        super(errorCode,message, errorMetaData, cause);
    }
}
