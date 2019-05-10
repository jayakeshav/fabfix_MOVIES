package edu.uci.ics.jkotha.service.movies.resources;

import edu.uci.ics.jkotha.service.movies.Models.MovieModel;
import edu.uci.ics.jkotha.service.movies.Models.SearchResponseModel;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("search")
public class SearchPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchFunction(@Context HttpHeaders headers,
                                   @DefaultValue("")@QueryParam("title") String title,
                                   @DefaultValue("")@QueryParam("genre") String genre,
                                   @QueryParam("year") int year,
                                   @DefaultValue("")@QueryParam("director") String director,
                                   @QueryParam("hidden") boolean hidden,
                                   @DefaultValue("0") @QueryParam("offset") int offset,
                                   @DefaultValue("10") @QueryParam("limit") int limit,
                                   @DefaultValue("rating") @QueryParam("orderby") String sortby,
                                   @DefaultValue("desc") @QueryParam("direction") String orderby

    ){
        ServiceLogger.LOGGER.info("search page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionId");
        SearchResponseModel responseModel;

        //make the queryParams with wild cards
        String xtitle = "%"+title+"%";
        String xgenre = "%"+genre+"%";
        String xyear;
        if (year>0)
            xyear = "%"+year+"%";
        else
            xyear = "%%";
        if (limit<=0)
            limit = 10;
        if (offset <=0)
            offset=0;
        if (!sortby.equals("rating")  & !sortby.equals("title"))
            sortby = "rating";
        if (!orderby.equals("asc") & !orderby.equals("desc"))
            orderby = "desc";
        String xdirector = "%"+director+"%";

        //users privilege
        boolean hasPrivilege = IdmRequests.hasPrivilegeLevelof3(email);
        String statement = "select  distinct  " +
                "m.id, m.year, m.title, m.director, m.budget, m.hidden ,m.backdrop_path ,m.overview, m.poster_path, m.revenue," +
                "r.numVotes, r.rating " +
                "from ((movies as m left join ratings as r on m.id = r.movieId) " +
                "left join genres_in_movies as gm on m.id = gm.movieId)" +
                "left join genres as g on g.id = gm.genreId " +
                "where r.movieId = m.id " +
                "and m.id =gm.movieId " +
                "and g.id =gm.genreId " +
                "and m.title like ? " +//1.title
                "and m.year like ? " +//2.year
                "and m.director like ? " +//3.director
                "and g.name like ? " +//4.genre
                "group by m.id " +
                "order by "+sortby+" "+orderby+" "+
                "limit ? offset ?";//5.limit 6.offset
        try {
            PreparedStatement query = MovieService.getCon().prepareStatement(statement);
            query.setString(1,xtitle);
            query.setString(2,xyear);
            query.setString(3,xdirector);
            query.setString(4,xgenre);
            query.setInt(5,limit);
            query.setInt(6,offset);
            ResultSet rs = query.executeQuery();
            MovieModel[] movies = FunctionsRequired.getMovieModelForSearch(rs,hasPrivilege);
            if (movies==null){
                ServiceLogger.LOGGER.info("response code:" + 211);
                responseModel = new SearchResponseModel(211,null);
                return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.info("response code:" + 210);
                responseModel = new SearchResponseModel(210,movies);
                return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }


        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).build();
    }
}
