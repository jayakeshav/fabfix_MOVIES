package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenreAddRequestModel {
    @JsonProperty(value = "name",required = true)
    private String name;

    @JsonCreator
    public GenreAddRequestModel(@JsonProperty(value = "name",required = true) String name) {
        this.name = name;
    }

    @JsonProperty
    public String getName() { return name; }
}
