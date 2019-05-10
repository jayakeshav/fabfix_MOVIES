package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarsSearchResponseModel {
    @JsonProperty(required = true)
    private int resultcode;
    @JsonProperty(required = true)
    private String message;
    private StarModel[] stars;

    @JsonCreator
    public StarsSearchResponseModel(int resultcode) {
        this.resultcode = resultcode;
        this.message = FunctionsRequired.getMessage(resultcode);
        this.stars = null;
    }

    @JsonCreator
    public StarsSearchResponseModel(int resultcode, StarModel[] stars) {
        this.resultcode = resultcode;
        this.message = FunctionsRequired.getMessage(resultcode);
        this.stars = stars;
    }

    @JsonProperty
    public int getResultcode() {
        return resultcode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public StarModel[] getStars() {
        return stars;
    }
}
