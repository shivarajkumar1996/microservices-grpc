package com.microservices.grpc.pojo;

import lombok.Getter;
import lombok.Setter;

/** Common Response object for successful POST and PUT requests. Required only on the client side  */
@Getter
@Setter
public class Response {
    private int id;
    private String statusMessage;
    private String path;

}
