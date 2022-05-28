package com.iemdb.Entity;


import com.iemdb.exception.AgeLimitError;
import com.iemdb.exception.MovieAlreadyExists;
import com.iemdb.exception.MovieNotFound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birthDate = birthDate;
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

    public List<Movie> getUserRecommendations(List<Movie> movies) {
        List<Movie> watchlist = getWatchList();
        Map<Movie, Double> scores = new HashMap<>();
        List<Movie> recommendations = new ArrayList<>();
        for (Movie movie : movies) {
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
