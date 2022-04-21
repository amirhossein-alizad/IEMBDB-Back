package com.iemdb.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class MovieNotFound extends RestException {
    public MovieNotFound(){ super("MovieNotFound"); }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
