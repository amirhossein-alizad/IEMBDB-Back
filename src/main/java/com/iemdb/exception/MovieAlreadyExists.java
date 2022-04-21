package com.iemdb.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class MovieAlreadyExists extends RestException {
    public MovieAlreadyExists(){ super("MovieAlreadyExists"); }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
