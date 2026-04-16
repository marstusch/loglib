package de.mtgz.logging.it;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/it/downstream")
@RegisterRestClient(configKey = "downstream")
public interface DownstreamClient {

   @GET
   @Path("/echo")
   @Produces(MediaType.TEXT_PLAIN)
   String echo();

   @GET
   @Path("/a")
   @Produces(MediaType.TEXT_PLAIN)
   String downstreamA();

   @GET
   @Path("/b")
   @Produces(MediaType.TEXT_PLAIN)
   String downstreamB();

   @GET
   @Path("/missing")
   @Produces(MediaType.TEXT_PLAIN)
   String missing();
}
