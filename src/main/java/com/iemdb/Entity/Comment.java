package com.iemdb.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemdb.Domain.CommentVote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Comment {
    private String userEmail;
    private int movieId;
    private String text;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private LocalDateTime time;
    private int like = 0;
    private int dislike = 0;
    @ElementCollection
    private List<String> users;
    @ElementCollection
    private List<Integer> votes;

    public Comment(String userEmail, int movieId, String text) {
        this.userEmail = userEmail;
        this.movieId = movieId;
        this.text = text;
        this.time = LocalDateTime.now();
    }


    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime() {
        this.time = LocalDateTime.now();
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields()) {
            String fieldName = f.getName();
            if (fieldName.equals("time") || fieldName.equals("users")
                    || fieldName.equals("votes") || fieldName.equals("id"))
                continue;
            if (f.get(this) == null)
                return true;
        }
        return false;
    }

    public void addVote(CommentVote commentVote) {
        if (users == null) {
            users = new ArrayList<>();
            votes = new ArrayList<>();
        }
        if (users.contains(commentVote.getUserEmail()))
            votes.set(users.indexOf(commentVote.getUserEmail()), commentVote.getVote());
        else {
            users.add(commentVote.getUserEmail());
            votes.add(commentVote.getVote());
        }
        like = Collections.frequency(votes, 1);
        dislike = Collections.frequency(votes, -1);
    }

}
