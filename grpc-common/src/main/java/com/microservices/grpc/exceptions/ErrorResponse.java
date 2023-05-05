package com.microservices.grpc.exceptions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Getter
@Setter
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME, visible = true)
@JsonTypeName("error")
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
    private Map<String, String> details;
}