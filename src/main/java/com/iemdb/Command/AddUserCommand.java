package com.iemdb.Command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemdb.Entity.User;

public class AddUserCommand implements Command {
    public AddUserCommand() {
    }

    @Override
    public void execute(String json, ObjectMapper objectMapper) throws Exception {
        try {
            User user = objectMapper.readValue(json, User.class);
            if (user.checkNull())
                throw new Exception();
        } catch (Exception exception) {
            throw new Exception("InvalidCommand");
        }
    }

}
