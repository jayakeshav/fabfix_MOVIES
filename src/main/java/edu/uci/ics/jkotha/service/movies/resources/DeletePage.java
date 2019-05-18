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
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("delete/{movieid}")
public class DeletePage {
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
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
            String s = "select hidden from movies where id  = ?";
            PreparedStatement q =MovieService.getCon().prepareStatement(s);
            q.setString(1,id);
            ResultSet rs = q.executeQuery();
            rs.next();
            int hidden = rs.getInt("hidden");

            String delete = "update movies set hidden = 1 where id=?";
            PreparedStatement deleteStatement = MovieService.getCon().prepareStatement(delete);
            deleteStatement.setString(1,id);
            int res = deleteStatement.executeUpdate();
            if (hidden==1){
                ServiceLogger.LOGGER.info("Result code:"+242+" with no of updates ="+res);
                responseModel = new DefaultResponseModel(242);
                return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
            }

            ServiceLogger.LOGGER.info("Result code:"+240+" with no of updates ="+res);
            responseModel = new DefaultResponseModel(240);
            return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            ServiceLogger.LOGGER.info("Result code:"+241);
            responseModel = new DefaultResponseModel(241);
            return Response.status(Response.Status.OK).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).entity(responseModel).build();
        }


//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionId",sessionId).header("transactionId",transactionId).build();

    }
}
