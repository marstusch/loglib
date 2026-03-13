package de.mtgz.logging.correlation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.container.ContainerRequestContext;

class CorrelationIdUtilTest {

   @Test
   void soll_headerwert_uebernehmen_wenn_correlationId_nicht_leer_ist() {
      String correlationId = CorrelationIdUtil.uebernehmenOderGenerieren("abc-123");

      assertThat(correlationId).isEqualTo("abc-123");
   }

   @Test
   void soll_correlationId_generieren_wenn_correlationId_leerer_string_ist() {
      String correlationId = CorrelationIdUtil.uebernehmenOderGenerieren("");

      assertThat(correlationId).isNotBlank();
      assertThat(correlationId).matches("[0-9a-fA-F-]{36}");
   }

   @Test
   void soll_correlationId_von_container_request_lesen() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      when(requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME)).thenReturn("corr-container");

      String correlationId = CorrelationIdUtil.getCorrelationIdFrom(requestContext);

      assertThat(correlationId).isEqualTo("corr-container");
   }

   @Test
   void soll_correlationId_von_client_request_lesen() {
      ClientRequestContext requestContext = mock(ClientRequestContext.class);
      when(requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME)).thenReturn("corr-client");

      String correlationId = CorrelationIdUtil.getCorrelationIdFrom(requestContext);

      assertThat(correlationId).isEqualTo("corr-client");
   }
}
