package de.mtgz.logging;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGenerator;
import de.mtgz.logging.context.LoggingContextConfig;
import de.mtgz.logging.context.LoggingContextService;
import de.mtgz.logging.correlation.CorrelationIdConstants;
import de.mtgz.logging.trace.TraceContextExtractor;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoggingResponseFilterTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_correlationId_aus_header_uebernehmen() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY)).thenReturn("incomingCorrId_123");
      when(responseContext.getHeaders()).thenReturn(headers);

      LoggingContextService service = new LoggingContextService(new LoggingContextConfig(),
         new TraceContextExtractor(), new IdGeneratorMock("generierteCorrId_wenn_Header_leer"));
      LoggingResponseFilter filter = new LoggingResponseFilter(service);

      filter.filter(requestContext, responseContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("incomingCorrId_123");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isNull();
   }

   @Test
   void soll_correlationId_aus_mdc_uebernehmen() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY)).thenReturn(null);
      when(responseContext.getHeaders()).thenReturn(headers);

      LoggingContextService service = new LoggingContextService(new LoggingContextConfig(),
         new TraceContextExtractor(), new IdGeneratorMock("generierteCorrId_wenn_MDC_leer"));
      LoggingResponseFilter filter = new LoggingResponseFilter(service);

      MDC.put(CorrelationIdConstants.MDC_KEY, "corrId123_ausMDC");

      filter.filter(requestContext, responseContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("corrId123_ausMDC");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
   }

   @Test
   void soll_correlationId_generieren_wenn_nicht_in_header_und_mdc_vorhanden() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY)).thenReturn(null);
      when(responseContext.getHeaders()).thenReturn(headers);

      LoggingContextService service = new LoggingContextService(new LoggingContextConfig(),
         new TraceContextExtractor(), new IdGeneratorMock("generierteCorrId123"));
      LoggingResponseFilter filter = new LoggingResponseFilter(service);

      filter.filter(requestContext, responseContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("generierteCorrId123");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
   }

   class IdGeneratorMock extends UuidGenerator {
      private final String id;

      public IdGeneratorMock(String id) {
         this.id = id;
      }

      @Override
      public String generate() {
         return this.id;
      }
   }
}
