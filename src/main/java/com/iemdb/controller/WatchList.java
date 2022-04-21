package com.iemdb.controller;

import com.iemdb.Entity.Movie;
import com.iemdb.Entity.User;
import com.iemdb.exception.LoginRequired;
import com.iemdb.exception.RestException;
import com.iemdb.model.IEMovieDataBase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class WatchList {

    @GetMapping("/watchlist")
    public ResponseEntity<List<Movie>> getWatchList() {
        try {
            List<Movie> watchlist = IEMovieDataBase.getInstance().getCurrentUser().getWatchList();
            return new ResponseEntity<>(watchlist, HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @PostMapping("/watchlist")
    public ResponseEntity<String> addToWatchlist(@RequestParam Map<String, String> input) {
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            int movie_id = Integer.parseInt(input.get("movie_id"));
            IEMovieDataBase.getInstance().addToWatchList(user.getEmail(), movie_id);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Movie has been added successfully!", HttpStatus.OK);
    }

    @PostMapping("/watchlist/remove")
    public ResponseEntity<String> removeFromWatchlist(@RequestParam Map<String, String> input) {
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            int movie_id = Integer.parseInt(input.get("movie_id"));
            IEMovieDataBase.getInstance().removeMovieFromWatchList(movie_id, user.getEmail());
            return new ResponseEntity<>("Movie removed successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Movie>> getRecommendation() {
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            List<Movie> recommendations = IEMovieDataBase.getInstance().getUserRecommendations(user.getEmail());
            return new ResponseEntity<>(recommendations, HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
