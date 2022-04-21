package com.iemdb.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class ActorNotFound extends RestException {
    public ActorNotFound() {super("ActorNotFound!");}

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
