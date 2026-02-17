package de.mtgz.logging.correlation;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.MDC;

import de.mtgz.logging.common.UuidGenerator;

/**
 * Client-Filter zur Weitergabe der CorrelationId bei Outbound-Requests.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationIdClientRequestFilter implements ClientRequestFilter {

   private final UuidGenerator idGenerator;

   /**
    * Erstellt den Filter mit Standard-Generator.
    */
   public CorrelationIdClientRequestFilter() {
      this(new UuidGenerator());
   }

   CorrelationIdClientRequestFilter(UuidGenerator idGenerator) {
      this.idGenerator = idGenerator;
   }

   /**
    * Fügt die CorrelationId in den Outbound-Request-Header ein.
    *
    * @param requestContext Request-Kontext
    */
   @Override
   public void filter(ClientRequestContext requestContext) {
      String correlationId = CorrelationIdUtil.getCorrelationIdFrom(requestContext);
      if (correlationId == null || correlationId.isBlank()) {
         Object correlationIdFromMDC = MDC.get(CorrelationIdConstants.MDC_KEY);
         correlationId = correlationIdFromMDC instanceof String ? (String) correlationIdFromMDC : null;
         if (correlationId == null || correlationId.isEmpty()) {
            correlationId = idGenerator.generate();
            MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
         }
      }
      requestContext.getHeaders().putSingle(CorrelationIdConstants.HEADER_NAME, correlationId);
   }
}