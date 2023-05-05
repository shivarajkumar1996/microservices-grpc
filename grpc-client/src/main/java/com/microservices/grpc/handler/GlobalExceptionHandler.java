package com.microservices.grpc.handler;

import com.microservices.grpc.exceptions.ErrorCode;
import com.microservices.grpc.exceptions.ErrorResponse;
import com.microservices.grpc.exceptions.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${server.trace:false}")
    private boolean printStackTrace;

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException cause) {
        log.error("Exception occured: {}", cause );
        return buildServiceErrorResponse(cause);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        return buildErrorResponse(exception);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception cause
    ) {
        var errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.INTERNAL_SERVER_ERROR);
        errorResponse.setMessage(cause.getMessage());
        errorResponse.setDetails(Map.of("cause", cause.getCause().toString()));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildServiceErrorResponse(
            ServiceException cause
    ) {
        var errorResponse = new ErrorResponse();
        var errorCode = cause.getErrorCode();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(cause.getMessage());
        errorResponse.setDetails(cause.getErrorMetaData());
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }



    private boolean isTraceOn(WebRequest request) {
        String [] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals("true");
    }
}
