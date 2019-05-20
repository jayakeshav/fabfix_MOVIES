package edu.uci.ics.jkotha.service.movies.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.movies.Models.DefaultResponseModel;
import edu.uci.ics.jkotha.service.movies.Models.StarAddMovieRequestModel;
import edu.uci.ics.jkotha.service.movies.Models.StarAddRequestModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.ServerIntermediates.IdmRequests;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;
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

@Path("star")
public class StarAddPage {
    @Path("/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addStarFun(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("add star page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionID");
        ObjectMapper mapper = new ObjectMapper();
        StarAddRequestModel requestModel;
        DefaultResponseModel responseModel;

        //check for privilege
        if(!IdmRequests.hasPrivilegeLevelof3(email)){
            ServiceLogger.LOGGER.info("Result code:"+141);
            responseModel = new DefaultResponseModel(141);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }
        try {
            requestModel = mapper.readValue(jsonText, StarAddRequestModel.class);

            //check for star exist;
            String check = "select count(*) as test from stars where name = ?";
            PreparedStatement checkQuery = MovieService.getCon().prepareStatement(check);
            checkQuery.setString(1,requestModel.getName());
            ResultSet rs = checkQuery.executeQuery();

            if (rs.next()){
                if (rs.getInt("test")==1){
                    ServiceLogger.LOGGER.info("Result code:"+222);
                    responseModel = new DefaultResponseModel(222);
                    return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
                }
            }

            //get the id to insert
            String id = "" ;
            String getId = "select max(id) as maxId from stars where id like 'ss%'";
            PreparedStatement getIdQuery = MovieService.getCon().prepareStatement(getId);
            ResultSet set = getIdQuery.executeQuery();
            if (set.next()){
                String input = set.getString("maxId");
                id = FunctionsRequired.getNewStarId(input);
            }

            //insert star
            String insert = "insert into stars(id, name, birthYear) values (?,?,?)";
            PreparedStatement insertStatement = MovieService.getCon().prepareStatement(insert);
            insertStatement.setString(1,id);
            insertStatement.setString(2,requestModel.getName());
            insertStatement.setInt(3,Integer.parseInt(requestModel.getBirthyear()));
            insertStatement.execute();
            ServiceLogger.LOGGER.info("Result code:"+220);
            responseModel = new DefaultResponseModel(220);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();

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
            ServiceLogger.LOGGER.info("Result code:"+221);
            responseModel = new DefaultResponseModel(221);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).build();
    }

    @Path("/starsin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addStarsToMovies(@Context HttpHeaders headers,String jsonText){
        ServiceLogger.LOGGER.info("add star page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionID");
        ObjectMapper mapper = new ObjectMapper();
        DefaultResponseModel responseModel;
        StarAddMovieRequestModel requestModel;

        //check for privilege
        if(!IdmRequests.hasPrivilegeLevelof3(email)){
            ServiceLogger.LOGGER.info("Result code:"+141);
            responseModel = new DefaultResponseModel(141);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }

        try {
            requestModel = mapper.readValue(jsonText, StarAddMovieRequestModel.class);

            //check if movieExits
            String check = "select count(*) as test from movies where id = ?";
            PreparedStatement checkQuery = MovieService.getCon().prepareStatement(check);
            checkQuery.setString(1,requestModel.getMovieid());
            ResultSet rs = checkQuery.executeQuery();

            if (rs.next()){
                if (rs.getInt("test")==1){
                    ServiceLogger.LOGGER.info("Result code:"+211);
                    responseModel = new DefaultResponseModel(211);
                    return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
                }
            }

            //check if star movie pair exists in db
            String check1 = "select count(*) as test from stars_in_movies where movieId = ? and starId = ?";
            PreparedStatement checkQuery1 = MovieService.getCon().prepareStatement(check1);
            checkQuery1.setString(1,requestModel.getMovieid());
            checkQuery1.setString(2,requestModel.getStarid());
            rs = checkQuery.executeQuery();

            if (rs.next()){
                if (rs.getInt("test")==1){
                    ServiceLogger.LOGGER.info("Result code:"+232);
                    responseModel = new DefaultResponseModel(232);
                    return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
                }
            }

            //insert star into movie
            String insert = "insert into stars_in_movies(starId, movieId) values (?,?)";
            PreparedStatement insertStatement = MovieService.getCon().prepareStatement(insert);
            insertStatement.setString(1,requestModel.getStarid());
            insertStatement.setString(2,requestModel.getMovieid());
            insertStatement.execute();
            ServiceLogger.LOGGER.info("Result code:"+230);
            responseModel = new DefaultResponseModel(230);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
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
            ServiceLogger.LOGGER.info("Result code:"+231);
            responseModel = new DefaultResponseModel(231);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).build();
    }

}
