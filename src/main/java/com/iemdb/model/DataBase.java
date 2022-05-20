package com.iemdb.model;

import com.iemdb.Domain.CommentVote;
import com.iemdb.Domain.MovieRating;
import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Comment;
import com.iemdb.Entity.Movie;
import com.iemdb.Entity.User;
import com.iemdb.Repository.ActorRepository;
import com.iemdb.Repository.CommentRepository;
import com.iemdb.Repository.MovieRepository;
import com.iemdb.exception.AgeLimitError;
import com.iemdb.exception.CommentNotFound;
import com.iemdb.exception.MovieAlreadyExists;
import com.iemdb.exception.MovieNotFound;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private List<Actor> actors;
    private List<Movie> movies;
    private List<User> users;
    private List<Comment> comments;

    ActorRepository actorRepository;
    MovieRepository movieRepository;
    CommentRepository commentRepository;

    public DataBase(ActorRepository actorRepository, MovieRepository movieRepository,
                    CommentRepository commentRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
        this.commentRepository = commentRepository;
        actors = new ArrayList<>();
        movies = new ArrayList<>();
        users = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public void addActor(Actor actor) {
        if (getActor(actor.getId()) == null) {
            actors.add(actor);
        }
        else {
            removeActor(actor.getId());
            actors.add(actor);
        }
        actorRepository.save(actor);
    }

    public void addMovie(Movie movie) {
        if (getMovie(movie.getId()) == null)
            movies.add(movie);
        else {
            removeMovie(movie.getId());
            movies.add(movie);
        }
        movieRepository.save(movie);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Actor> getActors() {
        return actors;
    }

    public Actor getActor(int id) {
        for (Actor actor : actors)
            if (actor.getId() == id)
                return actor;
        return null;
    }

    public List<Movie> getActorMovies(int actor_id) {
        List<Movie> movies_list = new ArrayList<>();
        for (Movie movie : movies)
            if (movie.getCast().contains(actor_id))
                movies_list.add(movie);
        return movies_list;
    }

    public User getUserByEmail(String email) {
        for (User user : users)
            if (user.getEmail().equals(email))
                return user;
        return null;
    }

    public void removeActor(int id) {
        for (int i = 0; i < actors.size(); i++)
            if (actors.get(i).getId() == id) {
                actors.remove(i);
                break;
            }
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Movie> filterMoviesByGenre(String genre) {
        List<Movie> movies = new ArrayList<>();
        for (Movie movie : getMovies())
            if (movie.getGenres().contains(genre))
                movies.add(movie);
        return movies;
    }

    public List<Movie> filterMoviesByYear(int startingYear, int finishingYear) {
        List<Movie> movies = new ArrayList<>();
        for (Movie movie : getMovies()) {
            LocalDate releaseDate = movie.getReleaseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (releaseDate.getYear() >= startingYear && releaseDate.getYear() <= finishingYear)
                movies.add(movie);
        }
        return movies;
    }

    public Movie getMovie(int id) {
        for (Movie movie : movies)
            if (movie.getId() == id)
                return movie;
        return null;
    }

    public void removeMovie(int id) {
        for (int i = 0; i < movies.size(); i++)
            if (movies.get(i).getId() == id) {
                movies.remove(i);
                break;
            }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Comment> getComments() {
        return comments;
    }

//    public void addCommentToMovie(Comment comment, int movieId, String userEmail) {
//        Movie movie = getMovie(movieId);
//        List<Comment> comments = movie.getComments();
//        for (int i = 0; i < comments.size(); i++)
//            if (comments.get(i).getUserEmail().equals(userEmail)) {
//                comments.set(i, comment);
//                return;
//            }
//        movie.addComment(comment);

//    }

    public void rateMovie(MovieRating movieRating) throws MovieNotFound {
        for (Movie movie : movies) {
            if (movie.getId() == movieRating.getMovieId()) {
                movie.addRating(movieRating);
                return;
            }
        }
        throw new MovieNotFound();
    }

    public void voteComment(CommentVote commentVote) throws CommentNotFound{
        for (Comment comment : comments) {
            if (comment.getId() == commentVote.getCommentId()) {
                comment.addVote(commentVote);
                return;
            }
        }
        throw new CommentNotFound();
    }

    public void addMovieToUserWatchList(int movieId, String userId) throws AgeLimitError, MovieAlreadyExists, MovieNotFound {
        Movie movie = getMovie(movieId);
        if (movie == null)
            throw new MovieNotFound();
        User user = getUserByEmail(userId);
        System.out.println(user);
        System.out.println(movie);
        user.addToWatchList(movie);
    }

    public void removeMovieFromUserWatchList(int movieId, String user_email) throws MovieNotFound {
        Movie movie = getMovie(movieId);
        User user = getUserByEmail(user_email);
        user.removeFromWatchList(movie);
    }
}
