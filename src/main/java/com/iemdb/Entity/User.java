package com.iemdb.Entity;


import com.iemdb.exception.AgeLimitError;
import com.iemdb.exception.MovieAlreadyExists;
import com.iemdb.exception.MovieNotFound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
    @Id
    private String email;
    private String password;
    private String nickname;
    private String name;
    private LocalDate birthDate;
    @ManyToMany
    private List<Movie> watchList;

    public User(String email, String password, String nickname, String name, LocalDate birthDate) {
        this.email = email;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            this.password = new String(hashBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        this.nickname = nickname;
        this.name = name;
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            this.password = new String(hashBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
        watchList.add(movie);
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
