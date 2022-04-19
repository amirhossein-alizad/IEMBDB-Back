package com.iemdb.exception;

import javax.servlet.ServletException;

public class UserNotFound extends ServletException {
    public UserNotFound(){ super("UserNotFound"); }
}
