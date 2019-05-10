package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StarAddMovieRequestModel {
    @JsonProperty(value = "starid",required = true)
    private String starid;
    @JsonProperty(value = "movieid",required = true)
    private String movieid;

    @JsonCreator
    public StarAddMovieRequestModel(
            @JsonProperty(value = "starid",required = true) String starid,
            @JsonProperty(value = "movieid",required = true) String movieid) {
        this.starid = starid;
        this.movieid = movieid;
    }

    @JsonProperty
    public String getStarid() { return starid; }

    @JsonProperty
    public String getMovieid() { return movieid; }
}
