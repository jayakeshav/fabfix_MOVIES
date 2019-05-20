package edu.uci.ics.jkotha.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.movies.Models.DefaultResponseModel;
import edu.uci.ics.jkotha.service.movies.Models.GenreAddRequestModel;
import edu.uci.ics.jkotha.service.movies.Models.GenresResponseModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.ServerIntermediates.IdmRequests;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("genre")
public class GenrePage {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response genrePage(
            @Context HttpHeaders headers
    ){
        ServiceLogger.LOGGER.info("/genre page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionID");
        GenresResponseModel responseModel;

        //check for privilege
        if(!IdmRequests.hasPrivilegeLevelof3(email)){
            ServiceLogger.LOGGER.info("Result code:"+141);
            responseModel = new GenresResponseModel(141);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }

        ServiceLogger.LOGGER.info("Result code:"+219);
        responseModel = new GenresResponseModel(219, FunctionsRequired.getGenres());
        return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();

//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).build();
    }

    @Path("/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response genreAdd(
            @Context HttpHeaders headers,String jsonText
    ){
        ServiceLogger.LOGGER.info("/genre/add page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionID");
        DefaultResponseModel responseModel;
        GenreAddRequestModel requestModel;
        //check for privilege
        if(!IdmRequests.hasPrivilegeLevelof3(email)){
            ServiceLogger.LOGGER.info("Result code:"+141);
            responseModel = new DefaultResponseModel(141);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, GenreAddRequestModel.class);

            String statement = "insert into genres(name) values (?)";
            PreparedStatement query = MovieService.getCon().prepareStatement(statement);
            query.setString(1,requestModel.getName());
            query.executeUpdate();

            ServiceLogger.LOGGER.info("Result code:"+217);//code genre add
            responseModel = new DefaultResponseModel(217);//code genre add
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
            }catch (SQLException e){
                ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
                ServiceLogger.LOGGER.info("Result code:"+218);//code genre could not add
                responseModel = new DefaultResponseModel(218);//code genre could not add
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
            }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).build();
    }

    @Path("/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response genreByMovieId(
            @PathParam("movieid") String id,
            @Context HttpHeaders headers
    ){
        ServiceLogger.LOGGER.info("genre/"+id+" requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionID");
        GenresResponseModel responseModel;

        //check for privilege
        if(!IdmRequests.hasPrivilegeLevelof3(email)){
            ServiceLogger.LOGGER.info("Result code:"+141);
            responseModel = new GenresResponseModel(141);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }

        try{
            String statement = "select g.id,g.name from genres as g , genres_in_movies as gm , movies as m where " +
                    "g.id =gm.genreId and gm.movieId = m.id and m.id = ?";
            PreparedStatement query = MovieService.getCon().prepareStatement(statement);
            query.setString(1,id);
            ResultSet rs = query.executeQuery();
            ServiceLogger.LOGGER.info("Result code:"+219);
            responseModel = new GenresResponseModel(219, FunctionsRequired.getGenres(rs));
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }catch (SQLException e){
            ServiceLogger.LOGGER.info("Result code:"+-1);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).build();
        }
    }



}
