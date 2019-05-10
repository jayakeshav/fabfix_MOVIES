package edu.uci.ics.jkotha.service.movies.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarIdResponseModel {
    @JsonProperty(required = true)
    private int resultcode;
    @JsonProperty(required = true)
    private String message;
    private StarModel star;

    @JsonCreator
    public StarIdResponseModel(int resultcode) {
        this.resultcode = resultcode;
        this.message = FunctionsRequired.getMessage(resultcode);
        this.star = null;
    }

    @JsonCreator
    public StarIdResponseModel(int resultcode, StarModel star) {
        this.resultcode = resultcode;
        this.message = FunctionsRequired.getMessage(resultcode);
        this.star = star;
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
    public StarModel getStars() {
        return star;
    }
}
