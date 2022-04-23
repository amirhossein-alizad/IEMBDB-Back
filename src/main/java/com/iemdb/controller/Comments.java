package com.iemdb.controller;

import com.iemdb.Entity.Comment;
import com.iemdb.Entity.User;
import com.iemdb.exception.RestException;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Comments {

    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> comments() {
        Utils.wait(2000);
        return new ResponseEntity<>(IEMovieDataBase.getInstance().getComments(), HttpStatus.OK);
    }

    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity<String> dislikeComment(@PathVariable int id) {
        Utils.wait(2000);
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            IEMovieDataBase.getInstance().voteComment(user.getEmail(), id, -1);
            return new ResponseEntity<>("Comment voted successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<String> likeComment(@PathVariable int id) {
        Utils.wait(2000);
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            IEMovieDataBase.getInstance().voteComment(user.getEmail(), id, 1);
            return new ResponseEntity<>("Comment voted successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(),e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
