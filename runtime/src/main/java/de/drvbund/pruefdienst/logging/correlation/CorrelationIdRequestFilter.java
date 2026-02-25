package de.drvbund.pruefdienst.logging.correlation;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.MDC;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class CorrelationIdRequestFilter implements ContainerRequestFilter {

   @Override
   public void filter(ContainerRequestContext requestContext) {
      String correlationId = CorrelationIdUtil.uebernehmenOderGenerieren(CorrelationIdUtil.getCorrelationIdFrom(requestContext));
      requestContext.setProperty(CorrelationIdConstants.REQUEST_PROPERTY, correlationId);
      MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
   }

}
