package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieIdResponseModel {
    @JsonProperty(required = true)
    private int resultCode;
    @JsonProperty(required = true)
    private String message;
    private MovieModel movie;


    @JsonCreator
    public MovieIdResponseModel(int resultCode, MovieModel movie) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.movie = movie;
    }

    @JsonCreator
    public MovieIdResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.movie = null;
    }



    @JsonProperty
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public MovieModel getMovie() {
        return movie;
    }
}
