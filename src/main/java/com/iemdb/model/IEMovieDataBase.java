package com.iemdb.model;

import com.iemdb.Domain.CommentVote;
import com.iemdb.Domain.MovieRating;
import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Comment;
import com.iemdb.Entity.Movie;
import com.iemdb.Entity.User;
import com.iemdb.exception.AgeLimitError;
import com.iemdb.exception.MovieAlreadyExists;
import com.iemdb.exception.MovieNotFound;
import com.iemdb.exception.UserNotFound;

import java.util.*;
import java.util.stream.Collectors;

public class IEMovieDataBase {
    private final DataBase dataBase;
    private final Initializer initializer;
    private User currentUser;

    private static IEMovieDataBase instance;

    private IEMovieDataBase(){
        dataBase = new DataBase();
        initializer = new Initializer(dataBase);
        initializer.getDataFromAPI();
    }

    public static IEMovieDataBase getInstance() {
        if(instance == null)
            instance = new IEMovieDataBase();
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getUser(String userId) throws UserNotFound{
        User user = dataBase.getUserByEmail(userId);
        if(user == null)
            throw new UserNotFound();
        return user;
    }

    public List<Movie> getMoviesByFilter(String filter, boolean sort) {
        List<Movie> movies = dataBase.getMovies();
        List<Movie> filtered = new ArrayList<>();
        if(!sort && filter.isEmpty())
            return movies;
        if(!sort) {
            for (Movie movie : movies)
                if (movie.getName().toLowerCase().contains(filter.toLowerCase()))
                    filtered.add(movie);
            return filtered;
        }
        else {
            if(filter.equals("rate"))
                movies.sort(Comparator.comparing(Movie::getImdbRate, Comparator.reverseOrder()));
            else
                movies.sort(Comparator.comparing(Movie::getReleaseDate, Comparator.reverseOrder()));
            return movies;
        }
    }

    public List<String> getMoviesActorsName(Movie movie) {
        return movie.getCast().stream().map(id -> dataBase.getActor(id).getName()).collect(Collectors.toList());
    }

    public List<Actor> getActors() {
        return dataBase.getActors();
    }

    public Movie getMovieById(int id){
        return dataBase.getMovie(id);
    }

    public List<Actor> getMoviesActors(Movie movie) {
        return movie.getCast().stream().map(dataBase::getActor).collect(Collectors.toList());
    }

    public void addToWatchList(String user_id, int movieId) throws AgeLimitError, MovieAlreadyExists {
        dataBase.addMovieToUserWatchList(movieId, user_id);
    }

    public void rateMovie(String user_id, int movie_id, int rating) {
        MovieRating movieRating = new MovieRating();
        movieRating.setUserEmail(user_id);
        movieRating.setMovieId(movie_id);
        movieRating.setScore(rating);
        dataBase.rateMovie(movieRating);
    }

    public void voteComment(String user_id, int comment_id, int vote) {
        CommentVote commentVote = new CommentVote();
        commentVote.setCommentId(comment_id);
        commentVote.setUserEmail(user_id);
        switch (vote) {
            case 1:
                commentVote.setVote("1");
                break;
            case 0:
                commentVote.setVote("0");
                break;
            case -1:
                commentVote.setVote("-1");
                break;
        }
        dataBase.voteComment(commentVote);
    }

    public void addComment(int movieId, String user, String text) {
        Comment comment = new Comment();
        comment.setMovieId(movieId);
        comment.setUserEmail(user);
        comment.setText(text);
        comment.setId();
        comment.setTime();
        dataBase.addComment(comment);
        dataBase.addCommentToMovie(comment, movieId, user);
    }

    public Actor getActorById(int actorId) {
        return dataBase.getActor(actorId);
    }

    public List<Movie> getActorMovies(int actorId) {
        return dataBase.getActorMovies(actorId);
    }

    public void removeMovieFromWatchList(int movieId, String userEmail) throws MovieNotFound {
        dataBase.removeMovieFromUserWatchList(movieId, userEmail);
    }

    public List<Movie> getUserRecommendations(String userEmail) {
        User user = dataBase.getUserByEmail(userEmail);
        List<Movie> watchlist = user.getWatchList();
        Map<Movie, Double> scores = new HashMap<>();
        List<Movie> recommendations = new ArrayList<>();
        for (Movie movie : dataBase.getMovies()) {
            if (watchlist.contains(movie))
                continue;
            Double genreSimilarity = Double.valueOf(movie.getGenreSimilarity(watchlist));
            Double score = movie.getImdbRate() + movie.getRating() + genreSimilarity;
            scores.put(movie, score);
        }
        List<Map.Entry<Movie, Double> > sorted_scores = new ArrayList<Map.Entry<Movie, Double>>(scores.entrySet());

        Collections.sort(sorted_scores, new Comparator<Map.Entry<Movie, Double> >() {
            public int compare(Map.Entry<Movie, Double> o1, Map.Entry<Movie, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        Collections.reverse(sorted_scores);
        for (Map.Entry<Movie, Double> entry : sorted_scores) {
            recommendations.add(entry.getKey());
            if (recommendations.size() == 3)
                break;
        }
        return recommendations;
    }
}
