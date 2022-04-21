package com.iemdb.controller;

import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Movie;
import com.iemdb.model.IEMovieDataBase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Actors {

    @GetMapping("/actors")
    public List<Actor> getActors() {
        return IEMovieDataBase.getInstance().getActors();
    }

    @GetMapping("/actors/{id}")
    public ResponseEntity<Actor> getActor(@PathVariable int id) {
        Actor actor = IEMovieDataBase.getInstance().getActorById(id);
        if (actor == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actor, HttpStatus.OK);
    }

    @GetMapping("/actors/{id}/movies")
    public ResponseEntity<List<Movie>> getActorMovies(@PathVariable int id) {
        try {
            Actor actor = IEMovieDataBase.getInstance().getActorById(id);
            if (actor == null)
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            List<Movie> movies = IEMovieDataBase.getInstance().getActorMovies(id);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
