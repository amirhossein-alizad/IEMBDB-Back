package com.iemdb.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iemdb.Domain.MovieRating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Movie {
    @Id
    private Integer id;
    private String name;
    @Column( length = 100000 )
    private String summary;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date releaseDate;
    private String director;
    @ElementCollection
    private List<String> writers;
    @ElementCollection
    private List<String> genres;
    @ElementCollection
    private List<Integer> cast;
    private double imdbRate;
    private long duration;
    private int ageLimit;
    @OneToMany
    private List<Comment> comments;
    @ElementCollection
    private List<String> user;
    @ElementCollection
    private List<Integer> userRating;
    private double rating;
    private String image;
    private String coverImage;

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
//
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
//
//    private int getNumberOfSimilarGenres(Movie movie) {
//        List<String> genres = getGenres();
//        return (int) genres.stream().filter(c -> movie.getGenres().contains(c)).count();
//    }
//
//    public int getGenreSimilarity(List<Movie> movies) {
//        int genreSimilarity = 0;
//        for (Movie movie : movies) {
//            genreSimilarity += getNumberOfSimilarGenres(movie);
//        }
//        return genreSimilarity;
//    }
}
