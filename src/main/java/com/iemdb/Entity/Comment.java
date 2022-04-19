package com.iemdb.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemdb.Domain.CommentVote;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Comment {
    private static int commentCount = 1;
    private String userEmail;
    private int movieId;
    private String text;
    private int id;
    private LocalDateTime time;
    private int like;
    private int dislike;
    private List<String> users;
    private List<Integer> votes;

    public Comment() {
    }

    public Comment(String userEmail, int movieId, String text) {
        this.userEmail = userEmail;
        this.movieId = movieId;
        this.text = text;
        this.id = commentCount;
        commentCount++;
        this.time = LocalDateTime.now();
    }

    public static int getCommentCount() {
        return commentCount;
    }

    public static void setCommentCount(int commentCount) {
        Comment.commentCount = commentCount;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId() {
        this.id = commentCount;
        commentCount++;
    }

    public void setTime() {
        this.time = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields()) {
            String fieldName = f.getName();
            if (fieldName.equals("time") || fieldName.equals("users")
                    || fieldName.equals("votes"))
                continue;
            if (f.get(this) == null)
                return true;
        }
        return false;
    }

    public void addVote(CommentVote commentVote) {
        if (users == null)
            users = new ArrayList<>();
        if (votes == null)
            votes = new ArrayList<>();
        if (users.contains(commentVote.getUserEmail()))
            votes.set(users.indexOf(commentVote.getUserEmail()), commentVote.getVote());
        else {
            users.add(commentVote.getUserEmail());
            votes.add(commentVote.getVote());
        }
        like = Collections.frequency(votes, 1);
        dislike = Collections.frequency(votes, -1);
    }

    public ObjectNode serialize() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode serialized_comment = mapper.createObjectNode();
        serialized_comment.put("commentId", getId());
        serialized_comment.put("userEmail", getUserEmail());
        serialized_comment.put("text", getText());
        serialized_comment.put("like", getLike());
        serialized_comment.put("dislike", getDislike());
        return serialized_comment;
    }

}
