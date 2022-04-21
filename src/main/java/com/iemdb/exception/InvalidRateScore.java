package com.iemdb.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class InvalidRateScore extends RestException {
    public InvalidRateScore() { super("InvalidRateScore"); }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
