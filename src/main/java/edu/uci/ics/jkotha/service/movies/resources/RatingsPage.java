package edu.uci.ics.jkotha.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.movies.Models.DefaultResponseModel;
import edu.uci.ics.jkotha.service.movies.Models.RatingsReqModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingsPage {
    @Path("rating")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ratingsPage(@Context HttpHeaders headers,String jsonText){
        ServiceLogger.LOGGER.info("stars page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        String transactionId = headers.getHeaderString("transactionID");
        ObjectMapper mapper = new ObjectMapper();
        RatingsReqModel requestModel;
        DefaultResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText,RatingsReqModel.class);
            String request = "select rating,numVotes from ratings where movieId = ?";
            PreparedStatement requestQuery = MovieService.getCon().prepareStatement(request);
            requestQuery.setString(1,requestModel.getId());
            ResultSet rs = requestQuery.executeQuery();
            if (rs.next()){
                int numVotes = rs.getInt("numVotes");
                float rating = rs.getFloat("rating");

                float newRating = ((rating*numVotes)+requestModel.getRating());
                newRating /= ++numVotes;

                String update = "update ratings set rating = ? and numVotes = ? where movieId = ?";
                PreparedStatement updateQuery = MovieService.getCon().prepareStatement(update);
                updateQuery.setFloat(1,newRating);
                updateQuery.setInt(2,numVotes);
                updateQuery.setString(3,requestModel.getId());
                updateQuery.execute();

                ServiceLogger.LOGGER.info("result code:"+250);
                responseModel = new DefaultResponseModel(250);
                return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();

            }
            else {
                ServiceLogger.LOGGER.info("result code:"+211);
                responseModel = new DefaultResponseModel(211);
                return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
            }
        }
        catch (IOException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.info("result code:"+-2);
                responseModel = new DefaultResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.info("result code:"+-3);
                responseModel = new DefaultResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
            }
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            ServiceLogger.LOGGER.info("result code:"+251);
            responseModel = new DefaultResponseModel(251);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();

        }


        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).build();

    }
}
