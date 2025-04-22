package io.simakkoi9.ratingservice.client;

import io.simakkoi9.ratingservice.exception.RideNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/rides")
@ApplicationScoped
@RegisterRestClient(configKey = "ridesClient")
public interface RidesClient {

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 5000,
            successThreshold = 2,
            skipOn = {RideNotFoundException.class}
    )
    @Retry(
            maxRetries = 3,
            delay = 200
    )
    @Timeout(1000)
    Response getRideById(@PathParam("id") String id);

}
