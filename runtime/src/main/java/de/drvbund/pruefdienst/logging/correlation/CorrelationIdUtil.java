package de.drvbund.pruefdienst.logging.correlation;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.container.ContainerRequestContext;

import de.drvbund.pruefdienst.logging.common.UuidGenerator;

public class CorrelationIdUtil {

   private static final UuidGenerator UUID_GENERATOR = new UuidGenerator();

   private CorrelationIdUtil() {
   }

   static String uebernehmenOderGenerieren(String correlationId) {
      if (correlationId == null || correlationId.isEmpty()) {
         return UUID_GENERATOR.generate();
      }
      return correlationId;
   }

   static String getCorrelationIdFrom(ContainerRequestContext requestContext) {
      return requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME);
   }

   static String getCorrelationIdFrom(ClientRequestContext requestContext) {
      return requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME);
   }
}
