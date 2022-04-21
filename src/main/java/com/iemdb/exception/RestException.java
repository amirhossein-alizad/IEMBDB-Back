package com.iemdb.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class RestException extends ServletException {
    public RestException() {super("Rest Exception");}

    public RestException(String message) {super(message);}

    public HttpStatus getStatusCode() {return HttpStatus.BAD_REQUEST;}
}
