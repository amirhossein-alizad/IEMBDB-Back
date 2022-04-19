package com.iemdb.Domain;

import java.lang.reflect.Field;

public class CommentVote {
    private String userEmail;
    private int commentId;
    private int vote;

    public CommentVote() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(String vote) {
        try {
            this.vote = Integer.parseInt(vote);
        } catch (Exception exception) {
        }
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields()) {
            if (f.get(this) == null)
                return true;
        }
        return false;
    }
}
