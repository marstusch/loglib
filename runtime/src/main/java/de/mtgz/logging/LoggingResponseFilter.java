package de.mtgz.logging;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.MDC;

import de.mtgz.logging.context.LoggingContextService;
import de.mtgz.logging.correlation.CorrelationIdConstants;

/**
 * Response-Filter zur Weitergabe der CorrelationId und zur Bereinigung des MDC.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class LoggingResponseFilter implements ContainerResponseFilter {

   private final LoggingContextService contextService;

   public LoggingResponseFilter() {
      this(new LoggingContextService());
   }

   LoggingResponseFilter(LoggingContextService contextService) {
      this.contextService = contextService;
   }

   /**
    * Schreibt die CorrelationId in den Response-Header und bereinigt den Kontext.
    *
    * @param requestContext Request-Kontext
    * @param responseContext Response-Kontext
    */
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
      Object property = requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY);
      String correlationId = property instanceof String ? (String) property : null;
      if (correlationId == null || correlationId.isBlank()) {
         Object fromMdc = MDC.get(CorrelationIdConstants.MDC_KEY);
         correlationId = fromMdc instanceof String ? (String) fromMdc : null;
      }
      correlationId = contextService.resolveCorrelationId(correlationId);
      responseContext.getHeaders().putSingle(CorrelationIdConstants.HEADER_NAME, correlationId);
      contextService.bereinigeMDC();
   }
}