package com.iemdb.Command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemdb.Entity.Comment;
import com.iemdb.Entity.Movie;
import com.iemdb.Entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class AddCommentCommand implements Command {
    public AddCommentCommand() {
    }

    @Override
    public void execute(String json, ObjectMapper objectMapper) throws Exception {
        try {
            Comment comment = objectMapper.readValue(json, Comment.class);
            if (comment.checkNull())
                throw new Exception();
        } catch (Exception exception) {
            throw new Exception("InvalidCommand");
        }
    }

    public void checkUser(List<User> users, String userEmail) throws Exception {
        List<String> usersEmails = users.stream().map(User::getEmail).collect(Collectors.toList());
        if (!usersEmails.contains(userEmail))
            throw new Exception("UserNotFound");
    }

    public void checkMovie(List<Movie> movies, Integer movieId) throws Exception {
        List<Integer> moviesIds = movies.stream().map(Movie::getId).collect(Collectors.toList());
        if (!moviesIds.contains(movieId))
            throw new Exception("MovieNotFound");
    }
}
