package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TesterModel {
    @JsonIgnore
    private Integer integer;
    @JsonIgnore
    private String test;

    @JsonCreator
    public TesterModel(Integer integer) {
        this.integer = integer;
        this.test = "test";
    }

    @JsonIgnore
    public Integer getInteger() {
        return integer;
    }

    @JsonIgnore
    public String getTest() {
        return test;
    }

    @JsonProperty
    @Override
    public String toString() {
        return "{\"id\":"+integer+",\""+test+"\"}";
    }
}
