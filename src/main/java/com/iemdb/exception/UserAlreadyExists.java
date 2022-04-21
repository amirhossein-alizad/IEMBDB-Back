package com.iemdb.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExists extends RestException {
    public UserAlreadyExists() {super("UserAlreadyExists");}

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
