package com.microservices.grpc.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private int id;
    private String statusMessage;
    private String path;

}
