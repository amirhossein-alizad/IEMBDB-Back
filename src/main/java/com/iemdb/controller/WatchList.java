package com.iemdb.controller;

import com.iemdb.Entity.Movie;
import com.iemdb.Entity.User;
import com.iemdb.Repository.MovieRepository;
import com.iemdb.Repository.UserRepository;
import com.iemdb.exception.LoginRequired;
import com.iemdb.exception.MovieNotFound;
import com.iemdb.exception.RestException;
import com.iemdb.model.CurrentUser;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
public class WatchList {

    private UserRepository userRepository;
    private MovieRepository movieRepository;

    @GetMapping("/watchlist")
    public ResponseEntity<List<Movie>> getWatchList() {
        Utils.wait(2000);
        try {
            Optional<User> user = userRepository.findById(CurrentUser.username);
            if(user.isEmpty())
                throw new LoginRequired();
            List<Movie> watchlist = user.get().getWatchList();
            return new ResponseEntity<>(watchlist, HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @PostMapping("/watchlist")
    public ResponseEntity<String> addToWatchlist(@RequestBody Map<String, String> input) {
        Utils.wait(2000);
        try {
            Optional<User> user = userRepository.findById(CurrentUser.username);
            if(user.isEmpty())
                throw new LoginRequired();
            int movie_id = Integer.parseInt(input.get("movie_id"));
            Optional<Movie> movie = movieRepository.findById(movie_id);
            if(movie.isEmpty())
                throw new MovieNotFound();
            User user1 = user.get();
            user1.addToWatchList(movie.get());
            userRepository.save(user1);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Movie has been added successfully!", HttpStatus.OK);
    }
//
    @PostMapping("/watchlist/remove")
    public ResponseEntity<String> removeFromWatchlist(@RequestBody Map<String, String> input) {
        Utils.wait(2000);
        try {
            Optional<User> user = userRepository.findById(CurrentUser.username);
            if(user.isEmpty())
                throw new LoginRequired();
            int movie_id = Integer.parseInt(input.get("movie_id"));
            Optional<Movie> movie = movieRepository.findById(movie_id);
            if(movie.isEmpty())
                throw new MovieNotFound();
            User user1 = user.get();
            user1.removeFromWatchList(movie.get());
            userRepository.save(user1);
            return new ResponseEntity<>("Movie removed successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
//
    @GetMapping("/recommendations")
    public ResponseEntity<List<Movie>> getRecommendation() {
        Utils.wait(2000);
        try {
            Optional<User> user = userRepository.findById(CurrentUser.username);
            if(user.isEmpty())
                throw new LoginRequired();
            User user1 = user.get();
            List<Movie> recommendations = user1.getUserRecommendations(StreamSupport.stream(movieRepository.findAll().spliterator(), false).collect(Collectors.toList()));
            return new ResponseEntity<>(recommendations, HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
