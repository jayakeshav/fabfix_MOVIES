package edu.uci.ics.jkotha.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.movies.Models.AddRequestModel;
import edu.uci.ics.jkotha.service.movies.Models.AddResponseModel;
import edu.uci.ics.jkotha.service.movies.Models.GenreModel;
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

@Path("add")
public class AddPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFunction(@Context HttpHeaders headers,String jsonText){
        ServiceLogger.LOGGER.info("add page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionId");

        AddRequestModel requestModel ;
        AddResponseModel responseModel ;

        ObjectMapper mapper = new ObjectMapper();
        try {
            //check for privilege
            if(!IdmRequests.hasPrivilegeLevelof3(email)){
                ServiceLogger.LOGGER.info("Result code:"+141);
                responseModel = new AddResponseModel(141);
                return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }

            requestModel = mapper.readValue(jsonText, AddRequestModel.class);
            //check for movie exist;
            String check = "select count(*) as test from movies where title = ?";
            PreparedStatement checkQuery = MovieService.getCon().prepareStatement(check);
            checkQuery.setString(1,requestModel.getTitle());
            ResultSet rs = checkQuery.executeQuery();

            if (rs.next()){
                if (rs.getInt("test")==1){
                    ServiceLogger.LOGGER.info("Result code:"+216);
                    responseModel = new AddResponseModel(216);
                    return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
                }
            }

            //get the id to insert
            String id = "";
            String getId = "select max(id) as maxId from movies where id like 'cs%'";
            PreparedStatement getIdQuery = MovieService.getCon().prepareStatement(getId);
            ResultSet set = getIdQuery.executeQuery();
            if (set.next()){
                String input = set.getString("maxId");
                id = FunctionsRequired.getNewMovieId(input);
            }

            String title = requestModel.getTitle();
            String director = requestModel.getDirector();
            int year = requestModel.getYear();
            String backdrop_path  = requestModel.getBackdrop_path();
            int budget = requestModel.getBudget();
            String overview = requestModel.getOverview();
            String poster_path = requestModel.getPoster_path();
            int revenue = requestModel.getRevenue();
            GenreModel[] Genres = requestModel.getGenres();

            //insert movie;
            String insert = "INSERT INTO movies (id, title, year, director, backdrop_path, budget, overview, poster_path, revenue, hidden) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
            PreparedStatement insertStatement = MovieService.getCon().prepareStatement(insert);
            insertStatement.setString(1,id);
            insertStatement.setString(2,title);
            insertStatement.setInt(3,year);
            insertStatement.setString(4,director);
            insertStatement.setString(5,backdrop_path);
            insertStatement.setInt(6,budget);
            insertStatement.setString(7,overview);
            insertStatement.setString(8,poster_path);
            insertStatement.setInt(9,revenue);
            insertStatement.execute();

            //insert Genres
            int[] genre = FunctionsRequired.insertGenres(Genres,id);
            if (genre.length != Genres.length){
                throw new SQLException("failed Genre Insertion");
            }

            ServiceLogger.LOGGER.info("Result code:"+214);
            responseModel = new AddResponseModel(214,id,genre);
            return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
        }
        catch (IOException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.info("result code:"+-2);
                responseModel = new AddResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.info("result code:"+-3);
                responseModel = new AddResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            ServiceLogger.LOGGER.info("Result code:"+215);
            responseModel = new AddResponseModel(215);
            return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).build();
    }
}
