package com.iemdb.model;

import com.iemdb.Domain.CommentVote;
import com.iemdb.Domain.MovieRating;
import com.iemdb.Entity.*;
import com.iemdb.exception.*;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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

    public User getCurrentUser() throws LoginRequired{
        if (currentUser == null)
            throw new LoginRequired();
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

    public User addUser(String email, String password, String name, String nickname, LocalDate birthDate) throws UserAlreadyExists {
        User user = null;
        try {
            user = getUser(email);
        } catch (UserNotFound ignored) {}
        if (user != null)
            throw new UserAlreadyExists();
        user = new User(email, password, nickname, name, birthDate);
        dataBase.addUser(user);
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

    public List<Movie> filterMovies(String searchText, String searchBy) throws NumberFormatException {
        if (searchText.equals(""))
            return dataBase.getMovies();
        List<Movie> filtered = new ArrayList<>();
        switch (searchBy) {
            case "name":
                for (Movie movie : dataBase.getMovies())
                    if (movie.getName().toLowerCase().contains(searchText.toLowerCase()))
                        filtered.add(movie);
                return filtered;
            case "genre":
                for (Movie movie : dataBase.getMovies())
                    if (movie.getGenres().contains(searchText))
                        filtered.add(movie);
                return filtered;
            case "date":
                int releaseDate = Integer.parseInt(searchText);
                for (Movie movie : dataBase.getMovies())
                    if (movie.getReleaseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() == releaseDate)
                        filtered.add(movie);
                return filtered;
            default:
                return dataBase.getMovies();
        }
    }

    public List<Movie> sortMovies(List<Movie> movies, String sortBy) {
        if (sortBy.equals("rate"))
            movies.sort(Comparator.comparing(Movie::getImdbRate, Comparator.reverseOrder()));
        else if(sortBy.equals("date"))
            movies.sort(Comparator.comparing(Movie::getReleaseDate, Comparator.reverseOrder()));
        return movies;
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

    public void addToWatchList(String user_id, int movieId) throws AgeLimitError, MovieAlreadyExists, MovieNotFound {
        dataBase.addMovieToUserWatchList(movieId, user_id);
    }

    public void rateMovie(String user_id, int movie_id, int rating) throws InvalidRateScore, MovieNotFound{
        MovieRating movieRating = new MovieRating();
        movieRating.setUserEmail(user_id);
        movieRating.setMovieId(movie_id);
        movieRating.setScore(rating);
        dataBase.rateMovie(movieRating);
    }

    public void voteComment(String user_id, int comment_id, int vote) throws InvalidVoteValue, CommentNotFound {
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
            default:
                throw new InvalidVoteValue();
        }
        dataBase.voteComment(commentVote);
    }

    public void addComment(int movieId, String user, String text) throws MovieNotFound {
        Comment comment = new Comment();
        Movie movie = dataBase.getMovie(movieId);
        if (movie == null)
            throw new MovieNotFound();
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

    public List<Comment> getComments() {
        return dataBase.getComments();
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
