package com.iemdb.Domain;

import com.iemdb.exception.InvalidRateScore;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;

@AllArgsConstructor
public class MovieRating {
    private String userEmail;
    private int movieId;
    private int score;

    public MovieRating() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) throws InvalidRateScore{
        if (score > 10 || score < 1)
            throw new InvalidRateScore();
        this.score = score;
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields()) {
            if (f.get(this) == null)
                return true;
        }
        return false;
    }
}
