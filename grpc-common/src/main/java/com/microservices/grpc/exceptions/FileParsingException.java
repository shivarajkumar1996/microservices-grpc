package com.microservices.grpc.exceptions;

import java.util.Map;

public class FileParsingException extends BaseException {
    public FileParsingException(String message, Map<String, String> errorMetaData) {
        this(ErrorCode.FILE_PARSING_ERROR,message, errorMetaData);
    }

    public FileParsingException(ErrorCode errorCode, String message, Map<String, String> errorMetaData) {
        super(errorCode,message, errorMetaData);
    }

    public FileParsingException(String message, Map<String, String> errorMetaData, Throwable cause) {
        this(ErrorCode.FILE_PARSING_ERROR,message, errorMetaData, cause);
    }

    public FileParsingException(ErrorCode errorCode,
                                String message, Map<String, String> errorMetaData, Throwable cause) {
        super(errorCode,message, errorMetaData, cause);
    }
}
