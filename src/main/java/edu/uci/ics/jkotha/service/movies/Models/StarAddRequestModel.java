package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StarAddRequestModel {
    @JsonProperty(value = "name",required = true)
    private String name;
    @JsonProperty(value = "birthYear", required = true)
    private String birthYear;

    @JsonCreator
    public StarAddRequestModel(
            @JsonProperty(value = "name",required = true) String name,
            @JsonProperty(value = "birthYear", required = true) String birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    @JsonProperty
    public String getName() { return name; }

    @JsonProperty
    public String getBirthYear() {
        return birthYear;
    }
}
