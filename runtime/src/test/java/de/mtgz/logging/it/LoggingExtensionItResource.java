package de.mtgz.logging.it;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.MDC;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.correlation.CorrelationIdConstants;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/it")
@Produces(MediaType.APPLICATION_JSON)
public class LoggingExtensionItResource {

   @Inject
   @RestClient
   ItDownstreamClient downstreamClient;

   @GET
   @Path("/ok")
   public Map<String, String> ok() {
      return mdcSnapshot();
   }

   @GET
   @Path("/call")
   public Map<String, Object> call() {
      ItDownstreamClient.DownstreamEcho downstream = downstreamClient.call("single");
      return Map.of(
         "requestCorrelationId", value(CorrelationIdConstants.MDC_KEY),
         "downstreamCorrelationId", downstream.correlationId());
   }

   @GET
   @Path("/fanout")
   public Map<String, Object> fanout() {
      ItDownstreamClient.DownstreamEcho a = downstreamClient.call("a");
      ItDownstreamClient.DownstreamEcho b = downstreamClient.call("b");

      return Map.of(
         "requestCorrelationId", value(CorrelationIdConstants.MDC_KEY),
         "downstreamA", a,
         "downstreamB", b);
   }

   @GET
   @Path("/downstream/{target}")
   public ItDownstreamClient.DownstreamEcho downstream(@PathParam("target") String target,
      @HeaderParam(CorrelationIdConstants.HEADER_NAME) String correlationId) {
      return new ItDownstreamClient.DownstreamEcho(target, correlationId);
   }

   @GET
   @Path("/boom")
   public Map<String, String> boom() {
      throw new RuntimeException("boom");
   }

   @GET
   @Path("/validation")
   public Map<String, String> validation() {
      throw new ConstraintViolationException("validation failed", Set.of());
   }

   private Map<String, String> mdcSnapshot() {
      Map<String, String> values = new LinkedHashMap<>();
      values.put(CorrelationIdConstants.MDC_KEY, value(CorrelationIdConstants.MDC_KEY));
      values.put(LoggingConstants.HTTP_METHOD_KEY, value(LoggingConstants.HTTP_METHOD_KEY));
      values.put(LoggingConstants.HTTP_PATH_KEY, value(LoggingConstants.HTTP_PATH_KEY));
      values.put(LoggingConstants.SERVICE_NAME_KEY, value(LoggingConstants.SERVICE_NAME_KEY));
      values.put(LoggingConstants.ENVIRONMENT_KEY, value(LoggingConstants.ENVIRONMENT_KEY));
      return values;
   }

   private String value(String key) {
      Object mdcValue = MDC.get(key);
      return mdcValue == null ? null : mdcValue.toString();
   }
}
