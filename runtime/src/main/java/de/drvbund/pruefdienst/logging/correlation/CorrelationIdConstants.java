package de.drvbund.pruefdienst.logging.correlation;

public final class CorrelationIdConstants {

   private CorrelationIdConstants() {
   }

   /**
    * Header-Name für die CorrelationId.
    */
   public static final String HEADER_NAME = "X-Correlation-Id";

   /**
    * MDC-Key für die CorrelationId.
    */
   public static final String MDC_KEY = "correlationId";

   public static final String REQUEST_PROPERTY = CorrelationIdConstants.class.getName() + ".id";

}
