package edu.uci.ics.jkotha.service.movies.resources;

import edu.uci.ics.jkotha.service.movies.Models.MovieIdResponseModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.ServerIntermediates.IdmRequests;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.movies.Models.MovieModel;
import edu.uci.ics.jkotha.service.movies.support.FunctionsRequired;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("get/{movieid}")
public class MoviePage {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFromId(@PathParam("movieid") String id,
                              @Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("movies/id= "+id+" requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionId");
        MovieIdResponseModel responseModel ;


        boolean privilegedUser = IdmRequests.hasPrivilegeLevelof4(email);
        String statement ="select m.id, m.year, m.title, m.director, m.budget, m.hidden, m.backdrop_path, m.overview, m.poster_path, m.revenue, r.rating, r.numVotes," +
                " group_concat(distinct concat(gm.genreId, '=', g.name) separator ';') as genre," +
                " group_concat(distinct concat(s.id, '=', s.name) separator ';') as stars" +
                " from (((((movies as m left join ratings as r on m.id = r.movieId)" +
                " left join genres_in_movies as gm on m.id = gm.movieId)" +
                " left join genres as g on gm.genreId = g.id)" +
                " left join stars_in_movies as sm on m.id = sm.movieId)" +
                " left join stars as s on sm.starId = s.id)" +
                " where m.id like ? " +//1.id
                "group by m.id";
        try {
            PreparedStatement query4 = MovieService.getCon().prepareStatement(statement);
            query4.setString(1,id);
            ResultSet rs = query4.executeQuery();
            if(rs.next()){
                if (!privilegedUser && (rs.getInt("hidden")==1)){
                    ServiceLogger.LOGGER.info("Result code:"+141);
                    responseModel = new MovieIdResponseModel(141);
                    return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).entity(responseModel).build();
                }
                if (privilegedUser){
                    MovieModel mm = new MovieModel(rs.getString("id"),rs.getString("title"),
                            rs.getString("director"),rs.getInt("year"),rs.getString("backdrop_path"),
                            rs.getInt("budget"),rs.getString("overview"),
                            rs.getString("poster_path"),rs.getInt("revenue"),rs.getFloat("rating"),
                            rs.getInt("numVotes"), FunctionsRequired.toGenreArray(rs.getString("genre")),
                            FunctionsRequired.getStarArray(rs.getString("stars")),rs.getInt("hidden"));
                    ServiceLogger.LOGGER.info("Result code:"+210);
                    responseModel = new MovieIdResponseModel(210,mm);
                    return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
                }
                else {
                    MovieModel mm = new MovieModel(rs.getString("id"),rs.getString("title"),
                            rs.getString("director"),rs.getInt("year"),rs.getString("backdrop_path"),
                            rs.getInt("budget"),rs.getString("overview"),
                            rs.getString("poster_path"),rs.getInt("revenue"),rs.getFloat("rating"),
                            rs.getInt("numVotes"), FunctionsRequired.toGenreArray(rs.getString("genre")),
                            FunctionsRequired.getStarArray(rs.getString("stars")),null);
                    ServiceLogger.LOGGER.info("Result code:"+210);
                    responseModel = new MovieIdResponseModel(210,mm);
                    return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
                }

            }
            else {
                ServiceLogger.LOGGER.info("Result code:"+211);
                responseModel = new MovieIdResponseModel(211);
                return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }
        }catch (Exception e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).build();
    }
}
