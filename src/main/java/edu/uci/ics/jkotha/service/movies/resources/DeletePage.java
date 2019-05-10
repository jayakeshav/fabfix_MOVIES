package edu.uci.ics.jkotha.service.movies.resources;


import edu.uci.ics.jkotha.service.movies.Models.DefaultResponseModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.ServerIntermediates.IdmRequests;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path("/{movieid}")
public class DeletePage {
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePage(@PathParam("movieid") String id,
                               @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("movies/delete = "+id+" requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionId");
        DefaultResponseModel responseModel ;

        //check for privilege
        if(!IdmRequests.hasPrivilegeLevelof3(email)){
            ServiceLogger.LOGGER.info("Result code:"+141);
            responseModel = new DefaultResponseModel(141);
            return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
        }
        // remove movie
        try {
            String delete = "update movies set hidden = 1 where id=?";
            PreparedStatement deleteStatement = MovieService.getCon().prepareStatement(delete);
            deleteStatement.setString(1,id);
            int res = deleteStatement.executeUpdate();
            ServiceLogger.LOGGER.info("Result code:"+217+" with no of updates ="+res);
            responseModel = new DefaultResponseModel(217);
            return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            ServiceLogger.LOGGER.info("Result code:"+218);
            responseModel = new DefaultResponseModel(218);
            return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
        }


//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).build();

    }
}
