package com.iemdb.Command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Movie;

import java.util.List;
import java.util.stream.Collectors;

public class AddMovieCommand implements Command {

    public AddMovieCommand() {
    }

    @Override
    public void execute(String json, ObjectMapper objectMapper) throws Exception {
        try {
            Movie movie = objectMapper.readValue(json, Movie.class);
            if (movie.checkNull())
                throw new Exception();
        } catch (Exception exception) {
            throw new Exception("InvalidCommand");
        }
    }

    public void checkActors(List<Actor> actors, List<Integer> cast) throws Exception {
        List<Integer> actorsIds = actors.stream().map(Actor::getId).collect(Collectors.toList());
        if (!actorsIds.containsAll(cast))
            throw new Exception("ActorNotFound");
    }

}
