package com.iemdb.exception;

import javax.servlet.ServletException;

public class MovieAlreadyExists extends ServletException {
    public MovieAlreadyExists(){ super("MovieAlreadyExists"); }
}
