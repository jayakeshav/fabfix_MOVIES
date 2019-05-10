package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarModel {
    @JsonProperty(required = true)
    private String id;
    @JsonProperty(required = true)
    private String name;
    @JsonProperty(required = true)
    private Integer birthYear;
    private MovieModelForStars[] movies;

    @JsonCreator
    public StarModel(String id, String name, Integer birthyear, MovieModelForStars[] movies) {
        this.id = id;
        this.name = name;
        if (birthyear==0)
            this.birthYear =null;
        else
            this.birthYear = birthyear;
        this.movies = movies;
    }

    @JsonCreator
    public StarModel(String id, String name, int birthyear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthyear;
        movies =null;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public Integer getBirthYear() {
        return birthYear;
    }

    @JsonProperty
    public MovieModelForStars[] getMovies() {
        return movies;
    }
}
