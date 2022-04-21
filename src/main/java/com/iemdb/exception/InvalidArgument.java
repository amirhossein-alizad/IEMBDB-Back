package com.iemdb.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class InvalidArgument extends RestException {
    public InvalidArgument(){ super("InvalidArgument"); }

    public InvalidArgument(String message) { super(message); }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
