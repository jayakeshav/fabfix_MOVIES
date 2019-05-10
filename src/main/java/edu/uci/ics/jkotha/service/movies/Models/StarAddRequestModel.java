package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StarAddRequestModel {
    @JsonProperty(value = "name",required = true)
    private String name;
    @JsonProperty(value = "birthyear",required = true)
    private String birthyear;

    @JsonCreator
    public StarAddRequestModel(
            @JsonProperty(value = "name",required = true) String name,
            @JsonProperty(value = "birthYear",required = true) String birthyear) {
        this.name = name;
        this.birthyear = birthyear;
    }

    @JsonProperty
    public String getName() { return name; }

    @JsonProperty
    public String getBirthyear() { return birthyear; }
}
