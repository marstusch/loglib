package de.drvbund.pruefdienst.logging;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import de.drvbund.pruefdienst.logging.context.LoggingContextService;
import de.drvbund.pruefdienst.logging.correlation.CorrelationIdConstants;

/**
 * Request-Filter zum Setzen der CorrelationId und der Pflichtfelder im MDC.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class LoggingRequestFilter implements ContainerRequestFilter {

   private final LoggingContextService contextService;

   public LoggingRequestFilter() {
      this(new LoggingContextService());
   }

   LoggingRequestFilter(LoggingContextService contextService) {
      this.contextService = contextService;
   }

   /**
    * Setzt CorrelationId und Logging-Kontext für eingehende Requests.
    *
    * @param requestContext Request-Kontext
    */
   @Override
   public void filter(ContainerRequestContext requestContext) {
      String headerValue = requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME);
      String correlationId = contextService.resolveCorrelationId(headerValue);
      requestContext.setProperty(CorrelationIdConstants.REQUEST_PROPERTY, correlationId);
      contextService.setzePflichtfelder(requestContext, correlationId);
   }
}
