package de.mtgz.logging.it;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.MDC;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.correlation.CorrelationIdConstants;

@Path("/it")
@Produces(MediaType.APPLICATION_JSON)
public class LoggingExtensionResource {

   @Inject
   @RestClient
   DownstreamClient downstreamClient;

   @GET
   @Path("/ok")
   public Map<String, String> ok() {
      return snapshot();
   }

   @GET
   @Path("/call")
   public Map<String, String> call() {
      String downstreamCorrelationId = downstreamClient.echo();
      Map<String, String> response = new LinkedHashMap<>();
      response.put("downstreamCorrelationId", downstreamCorrelationId);
      response.put("localCorrelationId", toStringValue(MDC.get(CorrelationIdConstants.MDC_KEY)));
      return response;
   }

   @GET
   @Path("/fanout")
   public Map<String, String> fanout() {
      String downstreamA = downstreamClient.downstreamA();
      String downstreamB = downstreamClient.downstreamB();

      Map<String, String> response = new LinkedHashMap<>();
      response.put("downstreamA", downstreamA);
      response.put("downstreamB", downstreamB);
      response.put("localCorrelationId", toStringValue(MDC.get(CorrelationIdConstants.MDC_KEY)));
      return response;
   }

   @GET
   @Path("/boom")
   public String boom() {
      throw new RuntimeException("boom");
   }

   @GET
   @Path("/validation")
   public String validation() {
      throw new ConstraintViolationException("validation failed", Set.<ConstraintViolation<?>>of());
   }

   @GET
   @Path("/http/forbidden")
   public String forbidden() {
      throw new WebApplicationException("forbidden", 403);
   }

   @GET
   @Path("/call-missing")
   public String callMissing() {
      return downstreamClient.missing();
   }

   @GET
   @Path("/downstream/echo")
   @Produces(MediaType.TEXT_PLAIN)
   public String downstreamEcho(@HeaderParam(CorrelationIdConstants.HEADER_NAME) String correlationId) {
      return correlationId;
   }

   @GET
   @Path("/downstream/a")
   @Produces(MediaType.TEXT_PLAIN)
   public String downstreamA(@HeaderParam(CorrelationIdConstants.HEADER_NAME) String correlationId) {
      return correlationId;
   }

   @GET
   @Path("/downstream/b")
   @Produces(MediaType.TEXT_PLAIN)
   public String downstreamB(@HeaderParam(CorrelationIdConstants.HEADER_NAME) String correlationId) {
      return correlationId;
   }

   private Map<String, String> snapshot() {
      Map<String, String> mdcSnapshot = new LinkedHashMap<>();
      mdcSnapshot.put(CorrelationIdConstants.MDC_KEY, toStringValue(MDC.get(CorrelationIdConstants.MDC_KEY)));
      mdcSnapshot.put(LoggingConstants.HTTP_METHOD_KEY, toStringValue(MDC.get(LoggingConstants.HTTP_METHOD_KEY)));
      mdcSnapshot.put(LoggingConstants.HTTP_PATH_KEY, toStringValue(MDC.get(LoggingConstants.HTTP_PATH_KEY)));
      mdcSnapshot.put(LoggingConstants.SERVICE_NAME_KEY, toStringValue(MDC.get(LoggingConstants.SERVICE_NAME_KEY)));
      mdcSnapshot.put(LoggingConstants.ENVIRONMENT_KEY, toStringValue(MDC.get(LoggingConstants.ENVIRONMENT_KEY)));
      return mdcSnapshot;
   }

   private String toStringValue(Object value) {
      return value == null ? null : String.valueOf(value);
   }
}
