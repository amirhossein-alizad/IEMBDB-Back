package com.iemdb.controller;

import com.iemdb.Entity.*;
import com.iemdb.exception.MovieNotFound;
import com.iemdb.exception.RestException;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import com.sun.source.tree.CatchTree;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Movies {

    @GetMapping("/movies")
    public List<Movie> getMovies() {
        Utils.wait(2000);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            User user = (User) request.getAttribute("user");
            System.out.println(user.getEmail());
        } catch (Exception e) {
            System.out.println("error occured: " + e.getMessage());
        }
        return IEMovieDataBase.getInstance().getMoviesByFilter("", false);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable int id) {
        Utils.wait(2000);
        Movie movie = IEMovieDataBase.getInstance().getMovieById(id);
        if (movie == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/movies/{id}/actors")
    public ResponseEntity<List<Actor>> getMovieActors(@PathVariable int id) {
        Utils.wait(2000);
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
    public ResponseEntity<List<Movie>> filterMovies(@RequestBody Map<String, String> input) {
        Utils.wait(2000);
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
    public ResponseEntity<String> rateMovie(@PathVariable int id, @RequestBody Map<String, String> input) {
        Utils.wait(2000);
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

    @PostMapping("/movies/{id}/comments")
    public ResponseEntity<String> commentOnMovie(@PathVariable int id, @RequestBody Map<String, String> input) {
        Utils.wait(2000);
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

    @GetMapping("/movies/{id}/comments")
    public ResponseEntity<List<Comment>> getMovieComments(@PathVariable int id) {
        Utils.wait(2000);
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            Movie movie = IEMovieDataBase.getInstance().getMovieById(id);
            if (movie == null)
                throw new MovieNotFound();
            return new ResponseEntity<>(movie.getComments(), HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


}
