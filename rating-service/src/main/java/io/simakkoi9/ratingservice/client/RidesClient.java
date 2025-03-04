package io.simakkoi9.ratingservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/rides")
@RegisterRestClient(configKey = "ridesClient")
public interface RidesClient {

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    JsonNode getRideById(@PathParam("id") String id) throws WebApplicationException;

}
