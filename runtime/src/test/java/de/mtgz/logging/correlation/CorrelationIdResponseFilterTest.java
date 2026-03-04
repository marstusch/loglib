package de.mtgz.logging.correlation;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CorrelationIdResponseFilterTest {

   private final CorrelationIdResponseFilter filter = new CorrelationIdResponseFilter();

   @AfterEach
   void tearDown() {
      MDC.remove(CorrelationIdConstants.MDC_KEY);
   }

   @Test
   void soll_correlationId_aus_request_header_fuer_response_uebernehmen_wenn_vorhanden() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY)).thenReturn("request-id");
      when(responseContext.getHeaders()).thenReturn(headers);

      filter.filter(requestContext, responseContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("request-id");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
   }

   @Test
   void soll_correlationId_aus_mdc_fuer_response_uebernehmen_wenn_keine_id_im_header_vorhanden() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY)).thenReturn(null);
      when(responseContext.getHeaders()).thenReturn(headers);
      MDC.put(CorrelationIdConstants.MDC_KEY, "mdc-id");

      filter.filter(requestContext, responseContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("mdc-id");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
   }

   @Test
   void soll_correlationId_generieren_wenn_header_und_mdc_leer() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getProperty(CorrelationIdConstants.REQUEST_PROPERTY)).thenReturn(null);
      when(responseContext.getHeaders()).thenReturn(headers);

      filter.filter(requestContext, responseContext);

      Object header = headers.getFirst(CorrelationIdConstants.HEADER_NAME);
      assertThat(header).isInstanceOf(String.class);
      assertThat((String) header).matches("[0-9a-fA-F-]{36}");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
   }
}