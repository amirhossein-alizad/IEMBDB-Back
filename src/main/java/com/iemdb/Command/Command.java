package com.iemdb.Command;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface Command {
    void execute(String json, ObjectMapper objectMapper) throws Exception;
}
