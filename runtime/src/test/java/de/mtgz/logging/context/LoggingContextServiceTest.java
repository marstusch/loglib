package de.mtgz.logging.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGenerator;
import de.mtgz.logging.correlation.CorrelationIdConstants;
import de.mtgz.logging.trace.TraceContext;
import de.mtgz.logging.trace.TraceContextExtractor;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;

class LoggingContextServiceTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_correlationId_aus_header_uebernehmen_wenn_nicht_leer() {
      LoggingContextService service = new LoggingContextService(new LoggingContextConfig(),
         new TraceContextExtractor(), new UuidGeneratorMock("generiert"));

      String correlationId = service.resolveCorrelationId("corr-header");

      assertThat(correlationId).isEqualTo("corr-header");
   }

   @Test
   void soll_correlationId_generieren_wenn_header_blank_ist() {
      LoggingContextService service = new LoggingContextService(new LoggingContextConfig(),
         new TraceContextExtractor(), new UuidGeneratorMock("generiert"));

      String correlationId = service.resolveCorrelationId("   ");

      assertThat(correlationId).isEqualTo("generiert");
   }

   @Test
   void soll_mdc_pflichtfelder_und_trace_felder_setzen() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      UriInfo uriInfo = mock(UriInfo.class);
      when(requestContext.getMethod()).thenReturn("POST");
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getPath()).thenReturn("/api/test");

      LoggingContextConfig contextConfig = mock(LoggingContextConfig.class);
      when(contextConfig.getServiceName()).thenReturn("logging-service");
      when(contextConfig.getEnvironment()).thenReturn("prod");

      TraceContextExtractor traceContextExtractor = mock(TraceContextExtractor.class);
      when(traceContextExtractor.extract()).thenReturn(new TraceContext("trace-id", "span-id"));

      LoggingContextService service = new LoggingContextService(contextConfig, traceContextExtractor, new UuidGeneratorMock("id"));
      service.setzePflichtfelder(requestContext, "corr-123");

      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo("corr-123");
      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isEqualTo("POST");
      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("/api/test");
      assertThat(MDC.get(LoggingConstants.SERVICE_NAME_KEY)).isEqualTo("logging-service");
      assertThat(MDC.get(LoggingConstants.ENVIRONMENT_KEY)).isEqualTo("prod");
      assertThat(MDC.get(LoggingConstants.TRACE_ID_KEY)).isEqualTo("trace-id");
      assertThat(MDC.get(LoggingConstants.SPAN_ID_KEY)).isEqualTo("span-id");
   }

   @Test
   void soll_mdc_defaultwerte_setzen_und_trace_felder_entfernen_wenn_kein_tracecontext_existiert() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      when(requestContext.getMethod()).thenReturn(" ");
      when(requestContext.getUriInfo()).thenReturn(null);

      LoggingContextConfig contextConfig = mock(LoggingContextConfig.class);
      when(contextConfig.getServiceName()).thenReturn("service-a");
      when(contextConfig.getEnvironment()).thenReturn("test");

      TraceContextExtractor traceContextExtractor = mock(TraceContextExtractor.class);
      when(traceContextExtractor.extract()).thenReturn(null);

      MDC.put(LoggingConstants.TRACE_ID_KEY, "alter-trace");
      MDC.put(LoggingConstants.SPAN_ID_KEY, "alter-span");

      LoggingContextService service = new LoggingContextService(contextConfig, traceContextExtractor, new UuidGeneratorMock("id"));
      service.setzePflichtfelder(requestContext, "corr-abc");

      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo("corr-abc");
      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isEqualTo("Unbekannt");
      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("/");
      assertThat(MDC.get(LoggingConstants.TRACE_ID_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.SPAN_ID_KEY)).isNull();
   }

   @Test
   void soll_pfad_auf_root_setzen_wenn_uri_path_leer_ist() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      UriInfo uriInfo = mock(UriInfo.class);
      when(requestContext.getMethod()).thenReturn("GET");
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getPath()).thenReturn("");

      TraceContextExtractor traceContextExtractor = mock(TraceContextExtractor.class);
      when(traceContextExtractor.extract()).thenReturn(null);

      LoggingContextConfig contextConfig = mock(LoggingContextConfig.class);
      when(contextConfig.getServiceName()).thenReturn("service-a");
      when(contextConfig.getEnvironment()).thenReturn("test");

      LoggingContextService service = new LoggingContextService(contextConfig, traceContextExtractor, new UuidGeneratorMock("id"));
      service.setzePflichtfelder(requestContext, "corr-xyz");

      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("/");
   }

   @Test
   void soll_bereinigen_mdc_felder_entfernen() {
      MDC.put(CorrelationIdConstants.MDC_KEY, "corr");
      MDC.put(LoggingConstants.HTTP_METHOD_KEY, "GET");
      MDC.put(LoggingConstants.HTTP_PATH_KEY, "/test");
      MDC.put(LoggingConstants.SERVICE_NAME_KEY, "service");
      MDC.put(LoggingConstants.ENVIRONMENT_KEY, "dev");
      MDC.put(LoggingConstants.TRACE_ID_KEY, "trace");
      MDC.put(LoggingConstants.SPAN_ID_KEY, "span");
      MDC.put(LoggingConstants.ERROR_ID_KEY, "error");

      LoggingContextService service = new LoggingContextService(new LoggingContextConfig(),
         new TraceContextExtractor(), new UuidGeneratorMock("id"));
      service.bereinigeMDC();

      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.SERVICE_NAME_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.ENVIRONMENT_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.TRACE_ID_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.SPAN_ID_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.ERROR_ID_KEY)).isNull();
   }

   static class UuidGeneratorMock extends UuidGenerator {
      private final String id;

      UuidGeneratorMock(String id) {
         this.id = id;
      }

      @Override
      public String generate() {
         return id;
      }
   }
}
