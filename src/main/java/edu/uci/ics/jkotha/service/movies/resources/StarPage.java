package edu.uci.ics.jkotha.service.movies.resources;


import edu.uci.ics.jkotha.service.movies.Models.StarIdResponseModel;
import edu.uci.ics.jkotha.service.movies.Models.StarModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
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

@Path("star")
public class StarPage {

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starIdPage(
            @Context HttpHeaders headers,
            @PathParam("id") String id
            )
    {
        ServiceLogger.LOGGER.info("star/id= "+id+" requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionId");
        StarIdResponseModel responseModel = null;

        String statement5 = "select id, name, birthYear from stars where id = ?";
        try {
            PreparedStatement query5 = MovieService.getCon().prepareStatement(statement5);
            query5.setString(1,id);
            ResultSet rs = query5.executeQuery();

            if (rs.next()){
                StarModel starModel = new StarModel(rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("birthYear"));
                responseModel = new StarIdResponseModel(212,starModel);
                ServiceLogger.LOGGER.info("Result code:"+212);
                return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.info("Result code:"+213);
                responseModel = new StarIdResponseModel(213);
                return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }

        }catch (Exception e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).build();
    }
}
