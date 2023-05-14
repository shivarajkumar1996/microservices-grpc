package com.microservices.grpc.handler;

import com.microservices.grpc.exceptions.ErrorCode;
import com.microservices.grpc.exceptions.ErrorResponse;
import com.microservices.grpc.exceptions.InvalidArgumentException;
import com.microservices.grpc.exceptions.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${server.trace:false}")
    private boolean printStackTrace;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        var errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.BAD_ARGUMENT);
        errorResponse.setMessage(ErrorCode.BAD_ARGUMENT.getMessage());
        HashMap validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        errorResponse.setDetails(validationErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException cause) {
        log.error("Exception occured: {}", cause );
        return buildServiceErrorResponse(cause);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        var errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.BAD_ARGUMENT);
        errorResponse.setMessage(e.getMessage());
        errorResponse.setDetails(Map.of("errors",e.getConstraintViolations().stream().map((cv) -> {
            return cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage();
        }).collect(Collectors.joining(", "))));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        var errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.BAD_ARGUMENT);
        errorResponse.setMessage("Invalid parameter value. Parameter name: " + ex.getName());
        errorResponse.setDetails(Map.of("parameterName", ex.getName(), "parameterValue",ex.getValue().toString(), "cause", ex.getCause().toString()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(InvalidArgumentException exception) {
        var errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCode.BAD_ARGUMENT);
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setDetails(exception.getErrorMetaData());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception exception) {
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
