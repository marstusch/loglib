package de.mtgz.logging.common;

public class LoggingConstants {
   /**
    * Header-Name für die ErrorId.
    */
   public static final String ERROR_ID_HEADER = "X-Error-Id";
   /**
    * MDC-Key für die HTTP-Methode.
    */
   public static final String HTTP_METHOD_KEY = "http.method";
   /**
    * MDC-Key für den HTTP-Pfad.
    */
   public static final String HTTP_PATH_KEY = "http.path";
   /**
    * MDC-Key für die Service-Bezeichnung.
    */
   public static final String SERVICE_NAME_KEY = "service";
   /**
    * MDC-Key für die Umgebung.
    */
   public static final String ENVIRONMENT_KEY = "environment";
   /**
    * MDC-Key für die TraceId (OpenTelemetry).
    */
   public static final String TRACE_ID_KEY = "traceId";
   /**
    * MDC-Key für die SpanId (OpenTelemetry).
    */
   public static final String SPAN_ID_KEY = "spanId";
   /**
    * MDC-Key für die ErrorId.
    */
   public static final String ERROR_ID_KEY = "errorId";

   private LoggingConstants() {
   }
}
