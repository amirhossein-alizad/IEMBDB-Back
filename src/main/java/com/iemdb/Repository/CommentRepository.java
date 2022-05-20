package com.iemdb.Repository;

import com.iemdb.Entity.Comment;
import com.iemdb.Entity.Movie;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
    List<Comment> findAllByMovieId(int id);
}
