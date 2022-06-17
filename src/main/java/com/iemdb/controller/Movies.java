package com.iemdb.controller;

import com.iemdb.Domain.MovieRating;
import com.iemdb.Entity.*;
import com.iemdb.Repository.ActorRepository;
import com.iemdb.Repository.CommentRepository;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@AllArgsConstructor
public class Movies {

    private MovieRepository movieRepository;
    private ActorRepository actorRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    @GetMapping("/movies")
    public List<Movie> getMovies() {
        
        return StreamSupport.stream(movieRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable int id) {
        
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(movie.get(), HttpStatus.OK);
    }
//
    @GetMapping("/movies/{id}/actors")
    public ResponseEntity<List<Actor>> getMovieActors(@PathVariable int id) {
        
        try {
            Optional<Movie> movie = movieRepository.findById(id);
            if (movie.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            Iterable<Actor> actors = actorRepository.findAllById(movie.get().getCast());
            return new ResponseEntity<>(StreamSupport.stream(actors.spliterator(), false).collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
//
//    @PostMapping("/movies")
//    public ResponseEntity<List<Movie>> filterMovies(@RequestBody Map<String, String> input) {
//        
//        try {
//            input.computeIfAbsent("searchText", key -> {throw new RuntimeException(key + " not found!");});
//            input.computeIfAbsent("searchBy", key -> {throw new RuntimeException(key + " not found!");});
//            input.computeIfAbsent("sortBy", key -> {throw new RuntimeException(key + " not found!");});
//            String searchText = input.get("searchText");
//            String searchBy = input.get("searchBy");
//            String sortBy = input.get("sortBy");
//            List<Movie> movies = IEMovieDataBase.getInstance().filterMovies(searchText, searchBy);
//            movies = IEMovieDataBase.getInstance().sortMovies(movies, sortBy);
//            return new ResponseEntity<>(movies, HttpStatus.OK);
//        } catch (Exception e) {
//            System.out.println(e);
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }
//
    @PostMapping("/movies/{id}/rate")
    public ResponseEntity<String> rateMovie(@PathVariable int id, @RequestBody Map<String, String> input) {
        
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            User user = (User) request.getAttribute("user");
            Optional<Movie> movie = movieRepository.findById(id);
            if(movie.isEmpty())
                throw new MovieNotFound();
            int rate = Integer.parseInt(input.get("quantity"));
            Movie movie1 = movie.get();
            movie1.addRating(new MovieRating(user.getEmail(), id, rate));
            movieRepository.save(movie1);
            return new ResponseEntity<>("Movie rated successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/movies/{id}/comments")
    public ResponseEntity<String> commentOnMovie(@PathVariable int id, @RequestBody Map<String, String> input) {
        
        try {
            input.computeIfAbsent("comment", key -> {throw new RuntimeException(key + " not found!");});
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            User user = (User) request.getAttribute("user");
            String comment = input.get("comment");
            Optional<Movie> movie = movieRepository.findById(id);
            if(movie.isEmpty())
                throw new MovieNotFound();
            Comment newComment = new Comment(user.getEmail(), id, comment);
            commentRepository.save(newComment);
            return new ResponseEntity<>("Comment added successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/movies/{id}/comments")
    public ResponseEntity<List<Comment>> getMovieComments(@PathVariable int id) {
        
        try {
            Optional<Movie> movie = movieRepository.findById(id);
            if (movie.isEmpty())
                throw new MovieNotFound();
            return new ResponseEntity<>(commentRepository.findAllByMovieId(movie.get().getId()), HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
