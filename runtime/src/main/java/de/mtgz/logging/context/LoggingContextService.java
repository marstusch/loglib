package de.mtgz.logging.context;

import java.util.Objects;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.MDC;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGenerator;
import de.mtgz.logging.correlation.CorrelationIdConstants;
import de.mtgz.logging.trace.TraceContext;
import de.mtgz.logging.trace.TraceContextExtractor;

/**
 * Befüllt und bereinigt Logging-Kontextinformationen im MDC.
 */
public class LoggingContextService {

   private final LoggingContextConfig config;
   private final TraceContextExtractor traceContextExtractor;
   private final UuidGenerator idGenerator;

   /**
    * Erstellt einen LoggingContextService mit Standardkonfiguration.
    */
   public LoggingContextService() {
      this(new LoggingContextConfig(), new TraceContextExtractor(), new UuidGenerator());
   }

   public LoggingContextService(LoggingContextConfig config,
      TraceContextExtractor traceContextExtractor,
      UuidGenerator idGenerator) {
      this.config = Objects.requireNonNull(config, "config");
      this.traceContextExtractor = Objects.requireNonNull(traceContextExtractor, "traceContextExtractor");
      this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator");
   }

   /**
    * Ermittelt eine CorrelationId oder generiert eine neue.
    *
    * @param headerValue eingehender Header-Wert
    *
    * @return CorrelationId
    */
   public String resolveCorrelationId(String headerValue) {
      if (headerValue == null || headerValue.isBlank()) {
         return idGenerator.generate();
      }
      return headerValue;
   }

   /**
    * Befüllt das MDC mit Pflichtfeldern für den aktuellen Request.
    *
    * @param requestContext Request-Kontext
    * @param correlationId CorrelationId
    */
   public void setzePflichtfelder(ContainerRequestContext requestContext, String correlationId) {
      MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
      MDC.put(LoggingConstants.HTTP_METHOD_KEY, normalisiere(requestContext.getMethod()));
      MDC.put(LoggingConstants.HTTP_PATH_KEY, normalisiere(requestContext.getUriInfo()));
      MDC.put(LoggingConstants.SERVICE_NAME_KEY, config.getServiceName());
      MDC.put(LoggingConstants.ENVIRONMENT_KEY, config.getEnvironment());

      TraceContext traceContext = traceContextExtractor.extract();
      if (traceContext != null) {
         MDC.put(LoggingConstants.TRACE_ID_KEY, traceContext.traceId());
         MDC.put(LoggingConstants.SPAN_ID_KEY, traceContext.spanId());
      } else {
         MDC.remove(LoggingConstants.TRACE_ID_KEY);
         MDC.remove(LoggingConstants.SPAN_ID_KEY);
      }
   }

   /**
    * Entfernt alle Logging-Kontextfelder aus dem MDC.
    */
   public void bereinigeMDC() {
      MDC.remove(CorrelationIdConstants.MDC_KEY);
      MDC.remove(LoggingConstants.HTTP_METHOD_KEY);
      MDC.remove(LoggingConstants.HTTP_PATH_KEY);
      MDC.remove(LoggingConstants.SERVICE_NAME_KEY);
      MDC.remove(LoggingConstants.ENVIRONMENT_KEY);
      MDC.remove(LoggingConstants.TRACE_ID_KEY);
      MDC.remove(LoggingConstants.SPAN_ID_KEY);
      MDC.remove(LoggingConstants.ERROR_ID_KEY);
   }

   private String normalisiere(String method) {
      if (method == null || method.isBlank()) {
         return "Unbekannt";
      }
      return method;
   }

   private String normalisiere(UriInfo uriInfo) {
      if (uriInfo == null) {
         return "/";
      }
      String path = uriInfo.getPath();
      if (path == null || path.isBlank()) {
         return "/";
      }
      return path;
   }
}
