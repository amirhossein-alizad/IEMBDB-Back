package com.iemdb.Command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemdb.Entity.Actor;

public class AddActorCommand implements Command {

    public AddActorCommand() {
    }

    @Override
    public void execute(String json, ObjectMapper objectMapper) throws Exception {
        try {
            Actor actor = objectMapper.readValue(json, Actor.class);
            if (actor.checkNull())
                throw new Exception();
        } catch (Exception exception) {
//            throw new Exception("InvalidCommand");
        }
    }
}
