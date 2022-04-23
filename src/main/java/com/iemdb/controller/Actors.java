package com.iemdb.controller;

import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Movie;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Actors {

    @GetMapping("/actors")
    public List<Actor> getActors() {
        Utils.wait(2000);
        return IEMovieDataBase.getInstance().getActors();
    }

    @GetMapping("/actors/{id}")
    public ResponseEntity<Actor> getActor(@PathVariable int id) {
        Utils.wait(2000);
        Actor actor = IEMovieDataBase.getInstance().getActorById(id);
        if (actor == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actor, HttpStatus.OK);
    }

    @GetMapping("/actors/{id}/movies")
    public ResponseEntity<List<Movie>> getActorMovies(@PathVariable int id) {
        Utils.wait(2000);
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
