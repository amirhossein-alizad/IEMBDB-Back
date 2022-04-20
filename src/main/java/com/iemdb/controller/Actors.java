package com.iemdb.controller;

import com.iemdb.Entity.Actor;
import com.iemdb.model.IEMovieDataBase;
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
    public Actor getActor(@PathVariable int id) {
        return IEMovieDataBase.getInstance().getActorById(id);
    }
}
