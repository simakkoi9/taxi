package io.simakkoi9.ratingservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/rides")
@RegisterRestClient
public interface RidesClient {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    JsonNode getRideById(@PathParam("id") String id);

}
