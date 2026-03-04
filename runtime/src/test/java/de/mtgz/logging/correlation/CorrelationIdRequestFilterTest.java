package de.mtgz.logging.correlation;

import jakarta.ws.rs.container.ContainerRequestContext;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorrelationIdRequestFilterTest {

   private final CorrelationIdRequestFilter filter = new CorrelationIdRequestFilter();

   @AfterEach
   void tearDown() {
      MDC.remove(CorrelationIdConstants.MDC_KEY);
   }

   @Test
   void soll_correlationId_aus_eingehenden_header_nutzen_wenn_vorhanden() {
      ContainerRequestContext context = mock(ContainerRequestContext.class);
      when(context.getHeaderString(CorrelationIdConstants.HEADER_NAME)).thenReturn("existing-id");

      filter.filter(context);

      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      verify(context).setProperty(eq(CorrelationIdConstants.REQUEST_PROPERTY), captor.capture());
      assertThat(captor.getValue()).isEqualTo("existing-id");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo("existing-id");
   }

   @Test
   void soll_correlationId_generieren_wenn_im_eingehenden_header_nicht_vorhanden() {
      ContainerRequestContext context = mock(ContainerRequestContext.class);
      when(context.getHeaderString(CorrelationIdConstants.HEADER_NAME)).thenReturn(null);

      filter.filter(context);

      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      verify(context).setProperty(eq(CorrelationIdConstants.REQUEST_PROPERTY), captor.capture());
      assertThat(captor.getValue()).isNotBlank();
      assertThat(captor.getValue()).matches("[0-9a-fA-F-]{36}");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo(captor.getValue());
   }
}