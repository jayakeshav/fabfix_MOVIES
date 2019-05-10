package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoveRequestModel {
    @JsonProperty(required = true)
    private String id;

    @JsonCreator
    public RemoveRequestModel(
            @JsonProperty(value = "id",required = true) String id) {
        this.id = id;
    }

    @JsonProperty
    public String getId() {
        return id;
    }
}
