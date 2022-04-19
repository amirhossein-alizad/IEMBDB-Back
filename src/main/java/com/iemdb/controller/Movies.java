package com.iemdb.controller;

import com.iemdb.Entity.Movie;
import com.iemdb.model.IEMovieDataBase;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class Movies {

    @GetMapping("/movies")
    public List<Movie> getMovies() {
        return IEMovieDataBase.getInstance().getMoviesByFilter("", false);
    }

    @GetMapping("/movies/{id}")
    public Movie getMovie(@PathVariable int id) {
        return IEMovieDataBase.getInstance().getMovieById(id);
    }

    @PostMapping("/moives")
    public


}
