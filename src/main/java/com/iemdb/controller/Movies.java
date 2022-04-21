package com.iemdb.controller;

import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Movie;
import com.iemdb.Entity.MovieId;
import com.iemdb.Entity.User;
import com.iemdb.exception.RestException;
import com.iemdb.model.IEMovieDataBase;
import jdk.jfr.Frequency;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
public class Movies {

    @GetMapping("/movies")
    public List<Movie> getMovies() {
        return IEMovieDataBase.getInstance().getMoviesByFilter("", false);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable int id) {
        Movie movie = IEMovieDataBase.getInstance().getMovieById(id);
        if (movie == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/movies/{id}/actors")
    public ResponseEntity<List<Actor>> getMovieActors(@PathVariable int id) {
        try {
            Movie movie = IEMovieDataBase.getInstance().getMovieById(id);
            if (movie == null)
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            List<Actor> actors = IEMovieDataBase.getInstance().getMoviesActors(movie);
            return new ResponseEntity<>(actors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/movies")
    public ResponseEntity<List<Movie>> filterMovies(@RequestParam Map<String, String> input) {
        try {
            input.computeIfAbsent("searchText", key -> {throw new RuntimeException(key + " not found!");});
            input.computeIfAbsent("searchBy", key -> {throw new RuntimeException(key + " not found!");});
            input.computeIfAbsent("sortBy", key -> {throw new RuntimeException(key + " not found!");});
            String searchText = input.get("searchText");
            String searchBy = input.get("searchBy");
            String sortBy = input.get("sortBy");
            List<Movie> movies = IEMovieDataBase.getInstance().filterMovies(searchText, searchBy);
            movies = IEMovieDataBase.getInstance().sortMovies(movies, sortBy);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/movies/{id}/rate")
    public ResponseEntity<String> rateMovie(@PathVariable int id, @RequestParam Map<String, String> input) {
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            int rate = Integer.parseInt(input.get("quantity"));
            IEMovieDataBase.getInstance().rateMovie(user.getEmail(), id, rate);
            return new ResponseEntity<>("Movie rated successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/movies/{id}/comment")
    public ResponseEntity<String> commentOnMovie(@PathVariable int id, @RequestParam Map<String, String> input) {
        try {
            input.computeIfAbsent("comment", key -> {throw new RuntimeException(key + " not found!");});
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            String comment = input.get("comment");
            IEMovieDataBase.getInstance().addComment(id, user.getEmail(), comment);
            return new ResponseEntity<>("Comment added successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
