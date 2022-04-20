package com.iemdb.controller;

import com.iemdb.Entity.Movie;
import com.iemdb.model.IEMovieDataBase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WatchList {

    @GetMapping("/watchlist")
    public List<Movie> getWatchList() {
        List<Movie> watchlist = IEMovieDataBase.getInstance().getCurrentUser().getWatchList();
        return IEMovieDataBase.getInstance().getCurrentUser().getWatchList();
    }

    @PostMapping("/watchlist")
    public String addToWatchlist() {
        return "";
    }
}
