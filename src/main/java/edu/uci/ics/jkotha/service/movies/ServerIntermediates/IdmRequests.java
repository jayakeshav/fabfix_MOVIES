package edu.uci.ics.jkotha.service.movies.ServerIntermediates;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.movies.Models.VerifyPrivilegeRequestModel;
import edu.uci.ics.jkotha.service.movies.Models.VerifyPrivilegeResponseModel;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class IdmRequests {

    public static boolean hasPrivilegeLevelof4(String email){ return isUserAllowedToMakeRequest(email,4); }

    public static boolean hasPrivilegeLevelof2(String email){
        return isUserAllowedToMakeRequest(email,2);
    }

    public static boolean hasPrivilegeLevelof3(String email){
        return isUserAllowedToMakeRequest(email,3);
    }

    private static boolean isUserAllowedToMakeRequest(String email, int plevel) {
        try {
            ServiceLogger.LOGGER.info("Verifying privilege level with IDM...");
            Client client = ClientBuilder.newClient();
            client.register(JacksonFeature.class);

            // Create a new Client
            ServiceLogger.LOGGER.info("Building client...");

            // Get the URI for the IDM
            ServiceLogger.LOGGER.info("Building URI...");
            String IDM_URI = MovieService.getMovieConfigs().getIdmConfigs().getIdmUri();

            ServiceLogger.LOGGER.info("Setting path to endpoint...");
            String IDM_ENDPOINT_PATH = MovieService.getMovieConfigs().getIdmConfigs().getPrivilegePath();

            // Create a WebTarget to send a request at
            ServiceLogger.LOGGER.info("Building WebTarget...");
            WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);

            // Create an InvocationBuilder to create the HTTP request
            ServiceLogger.LOGGER.info("Starting invocation builder...");
            Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);

            // Set the payload
            ServiceLogger.LOGGER.info("Setting payload of the request");
            VerifyPrivilegeRequestModel requestModel = new VerifyPrivilegeRequestModel(email, plevel);


            // Send the request and save it to a Response
            ServiceLogger.LOGGER.info("Sending request...");
            Response response = builder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
            ServiceLogger.LOGGER.info("Sent!");

            // Check that status code of the request
            if (response.getStatus() == 200) {
                ServiceLogger.LOGGER.info("received status 200!");
                VerifyPrivilegeResponseModel responseModel = null;
                String jsonText = response.readEntity(String.class);
                ObjectMapper mapper = new ObjectMapper();
                responseModel = mapper.readValue(jsonText,VerifyPrivilegeResponseModel.class);
                if (responseModel.getResultCode() == 140) {
                    ServiceLogger.LOGGER.warning(responseModel.getMessage());
                    return true;
                } else {
                    ServiceLogger.LOGGER.warning(responseModel.getMessage());
                    return false;
                }
            } else {
                ServiceLogger.LOGGER.warning("received status " + response.getStatus());
                return false;


            }
        }
        catch (Exception e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            return false;
        }
    }
}
