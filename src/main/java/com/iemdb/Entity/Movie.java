package com.iemdb.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iemdb.Domain.MovieRating;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Movie {
    private Integer id;
    private String name;
    private String summary;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date releaseDate;
    private String director;
    private List<String> writers;
    private List<String> genres;
    private List<Integer> cast;
    private double imdbRate;
    private long duration;
    private int ageLimit;
    private List<Comment> comments;
    private List<String> user;
    private List<Integer> userRating;
    private double rating;
    private String image;
    private String coverImage;

    public Movie(Integer id, String name, String summary, Date releaseDate, String director,
                 List<String> writers, List<String> genres, List<Integer> cast, double imdbRate,
                 long duration, int ageLimit, String image, String coverImage) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.releaseDate = releaseDate;
        this.director = director;
        this.writers = writers;
        this.genres = genres;
        this.cast = cast;
        this.imdbRate = imdbRate;
        this.duration = duration;
        this.ageLimit = ageLimit;
        comments = new ArrayList<>();
        user = new ArrayList<>();
        userRating = new ArrayList<>();
        rating = 0.0;
        this.image = image;
        this.coverImage = coverImage;
    }

    public Movie() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getWriters() {
        return writers;
    }

    public void setWriters(List<String> writers) {
        this.writers = writers;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<Integer> getCast() {
        return cast;
    }

    public void setCast(List<Integer> cast) {
        this.cast = cast;
    }

    public double getImdbRate() {
        return imdbRate;
    }

    public void setImdbRate(double imdbRate) {
        this.imdbRate = imdbRate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<Comment> getComments() {
        if (comments == null)
            return new ArrayList<>();
        return comments;
    }

    public List<String> getUser() {
        return user;
    }

    public List<Integer> getUserRating() {
        return userRating;
    }

    public double getRating() {
        return rating;
    }

    public void addComment(Comment comment) {
        if (comments == null)
            comments = new ArrayList<>();
        comments.add(comment);
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields()) {
            String fieldName = f.getName();
            if (fieldName.equals("comments") || fieldName.equals("user")
                    || fieldName.equals("userRating"))
                continue;
            if (f.get(this) == null)
                return true;
        }
        return false;
    }

    public void addRating(MovieRating movieRating) {
        if (user == null)
            user = new ArrayList<>();
        if (userRating == null)
            userRating = new ArrayList<>();
        if (user.contains(movieRating.getUserEmail()))
            userRating.set(user.indexOf(movieRating.getUserEmail()), movieRating.getScore());
        else {
            userRating.add(movieRating.getScore());
            user.add(movieRating.getUserEmail());
        }
        rating = userRating.stream().mapToDouble(Integer::intValue).sum() / userRating.size();
    }

    private int getNumberOfSimilarGenres(Movie movie) {
        List<String> genres = getGenres();
        return (int) genres.stream().filter(c -> movie.getGenres().contains(c)).count();
    }

    public int getGenreSimilarity(List<Movie> movies) {
        int genreSimilarity = 0;
        for (Movie movie : movies) {
            genreSimilarity += getNumberOfSimilarGenres(movie);
        }
        return genreSimilarity;
    }
}
