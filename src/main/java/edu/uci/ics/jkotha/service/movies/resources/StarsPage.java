package edu.uci.ics.jkotha.service.movies.resources;

import edu.uci.ics.jkotha.service.movies.Models.StarModel;
import edu.uci.ics.jkotha.service.movies.Models.StarsSearchResponseModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
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

@Path("star")
public class StarsPage {
    @Path("/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starsPage(
            @Context HttpHeaders headers,
            @DefaultValue("") @QueryParam("name") String name,
            @DefaultValue("") @QueryParam("movieTitle") String movieTitle,
            @QueryParam("birthYear") int year,
            @DefaultValue("10") @QueryParam("limit") int limit,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("name") @QueryParam("orderby") String sortby,
            @DefaultValue("asc") @QueryParam("direction") String orderby
    ){
        ServiceLogger.LOGGER.info("stars page requested");
        StarsSearchResponseModel responseModel = null;
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        String transactionId = headers.getHeaderString("transactionID");

        if (limit<=0)
            limit = 10;
        if (offset <=0)
            offset=0;
        if (!sortby.equals("name") & !sortby.equals("birthYear"))
            sortby = "name";
        if (!orderby.equals("asc") & !orderby.equals("desc"))
            orderby = "desc";

        String xname = "%"+name+"%";
        String xmovieTitle = "%"+movieTitle+"%";
        String xyear;
        if (year>0)
            xyear = "%"+year+"%";
        else
            xyear = "%%";

        String secondarySortBy, secondaryOrderBy;
        if (sortby.equalsIgnoreCase("name")) {
            secondarySortBy = "birthYear";
            secondaryOrderBy = "asc";
        } else {
            secondarySortBy = "name";
            secondaryOrderBy = "asc";
        }

        try {
            String ps = "select distinct s.id, s.name, s.birthYear " +
                    "from (stars as s left join stars_in_movies as sm on s.id = sm.starId) " +
                    "left join movies as m on m.id = sm.movieId " +
                    "where s.name like ? " +//1.name
                    "and s.birthYear like ? " +//2.year
                    "and m.title like ? " +//3.title
                    "order by " + sortby + " " + orderby + "," + secondarySortBy + " " + secondaryOrderBy + " " +
                    "limit ? offset ? ";//4.limit 5.offset
            PreparedStatement query3 = MovieService.getCon().prepareStatement(ps);
            query3.setString(1,xname);
            query3.setString(2,xyear);
            query3.setString(3,xmovieTitle);
            query3.setInt(4,limit);
            query3.setInt(5,offset);
            ServiceLogger.LOGGER.info("request query" + query3.toString());
            ResultSet rs = query3.executeQuery();
            StarModel[] result = FunctionsRequired.getStarModel(rs);
            if (result==null){
                responseModel = new StarsSearchResponseModel(213);
            }
            else {
                responseModel = new StarsSearchResponseModel(212, result);
            }
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).entity(responseModel).build();
        }catch (Exception e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionId", sessionId).header("transactionID", transactionId).build();
    }
}
