package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenresResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "genres")
    private GenreModel[] genres;

    @JsonCreator
    public GenresResponseModel(int resultCode, GenreModel[] genres) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.genres = genres;
    }

    @JsonCreator
    public GenresResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.genres = null;
    }

    @JsonProperty
    public int getResultCode() { return resultCode; }

    @JsonProperty
    public String getMessage() { return message; }

    @JsonProperty
    public GenreModel[] getGenres() { return genres; }
}
