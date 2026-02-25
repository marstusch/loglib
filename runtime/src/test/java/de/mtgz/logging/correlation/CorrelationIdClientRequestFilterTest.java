package de.mtgz.logging.correlation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.mtgz.logging.common.UuidGenerator;

class CorrelationIdClientRequestFilterTest {
   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_correlationId_aus_mdc_nutzen_wenn_vorhanden() {
      ClientRequestContext requestContext = mock(ClientRequestContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getHeaders()).thenReturn(headers);

      MDC.put(CorrelationIdConstants.MDC_KEY, "existing");

      CorrelationIdClientRequestFilter filter = new CorrelationIdClientRequestFilter(new UuidGenerator());
      filter.filter(requestContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("existing");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo("existing");
   }

   @Test
   void soll_correlationId_aus_header_nutzen_wenn_vorhanden_und_mdc_ignorieren() {
      ClientRequestContext requestContext = mock(ClientRequestContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getHeaders()).thenReturn(headers);
      when(requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME)).thenReturn("corrId_imHeader");

      CorrelationIdClientRequestFilter filter = new CorrelationIdClientRequestFilter(new UuidGenerator());
      filter.filter(requestContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("corrId_imHeader");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
   }

   @Test
   void soll_correlationId_generieren_wenn_nicht_im_MDC_vorhanden() {
      ClientRequestContext requestContext = mock(ClientRequestContext.class);
      MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
      when(requestContext.getHeaders()).thenReturn(headers);

      CorrelationIdClientRequestFilter filter = new CorrelationIdClientRequestFilter(new UuidGeneratorMock("generated"));
      filter.filter(requestContext);

      assertThat(headers.getFirst(CorrelationIdConstants.HEADER_NAME)).isEqualTo("generated");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo("generated");
   }

   static class UuidGeneratorMock extends UuidGenerator {
      private final String id;

      public UuidGeneratorMock(String id) {
         this.id = id;
      }

      @Override
      public String generate() {
         return this.id;
      }
   }
}
