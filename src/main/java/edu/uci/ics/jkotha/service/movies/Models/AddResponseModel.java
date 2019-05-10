package edu.uci.ics.jkotha.service.movies.Models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddResponseModel {

    @JsonProperty(required = true)
    private int resultCode;
    @JsonProperty(required = true)
    private String message;
    private String movieid;
    private int[] genreid;

    @JsonCreator
    public AddResponseModel(int resultCode, String movieid, int[] genreid) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.movieid = movieid;
        this.genreid = genreid;
    }

    @JsonCreator
    public AddResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = FunctionsRequired.getMessage(resultCode);
        this.movieid = null;
        this.genreid = null;
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
    public String getMovieid() {
        return movieid;
    }

    @JsonProperty
    public int[] getGenreid() {
        return genreid;
    }
}
