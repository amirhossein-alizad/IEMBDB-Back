package com.iemdb.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class InvalidVoteValue extends RestException {
    public InvalidVoteValue() { super("InvalidVoteValue"); }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
