package de.mtgz.logging.correlation;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.MDC;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationIdResponseFilter implements ContainerResponseFilter {

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
      Object correlationIdFromRequest = requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY);
      String correlationId = correlationIdFromRequest instanceof String ? (String) correlationIdFromRequest : null;
      if (correlationId == null) {
         Object correlationIdFromMDC = MDC.get(CorrelationIdConstants.MDC_KEY);
         correlationId = correlationIdFromMDC instanceof String ? (String) correlationIdFromMDC : null;
      }
      correlationId = CorrelationIdUtil.uebernehmenOderGenerieren(correlationId);
      responseContext.getHeaders().putSingle(CorrelationIdConstants.HEADER_NAME, correlationId);
      MDC.remove(CorrelationIdConstants.MDC_KEY);
   }
}