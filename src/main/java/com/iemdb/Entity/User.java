package com.iemdb.Entity;


import com.iemdb.exception.AgeLimitError;
import com.iemdb.exception.MovieAlreadyExists;
import com.iemdb.exception.MovieNotFound;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private LocalDate birthDate;
    private List<Movie> watchList;

    public User() {
    }

    public User(String email, String password, String nickname, String name, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public List<Movie> getWatchList() {
        if (watchList == null)
            return new ArrayList<>();
        return watchList;
    }

    public void addToWatchList(Movie movie) throws AgeLimitError, MovieAlreadyExists {
        if (watchList == null)
            watchList = new ArrayList<>();
        if (watchList.contains(movie))
            throw new MovieAlreadyExists();
        if (movie.getAgeLimit() > (LocalDate.now().getYear() - birthDate.getYear()))
            throw new AgeLimitError();
        System.out.println("movie added to watchlist");
        watchList.add(movie);
        System.out.println(watchList);
    }

    public void removeFromWatchList(Movie movie) throws MovieNotFound {
        if (watchList == null)
            watchList = new ArrayList<>();
        if (!watchList.contains(movie))
            throw new MovieNotFound();
        watchList.remove(movie);
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields())
            if (f.get(this) == null && !f.getName().equals("watchList"))
                return true;
        return false;
    }
}
