package de.mtgz.logging.it;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/it/downstream")
@RegisterRestClient(configKey = "it-downstream")
public interface ItDownstreamClient {

   @GET
   @Path("/{target}")
   @Produces(MediaType.APPLICATION_JSON)
   DownstreamEcho call(@PathParam("target") String target);

   record DownstreamEcho(String target, String correlationId) {
   }
}
