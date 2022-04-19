package com.iemdb.exception;

import javax.servlet.ServletException;

public class AgeLimitError extends ServletException {
    public AgeLimitError(){ super("AgeLimitError"); }
}
