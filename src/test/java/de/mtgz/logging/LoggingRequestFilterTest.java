package de.mtgz.logging;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGenerator;
import de.mtgz.logging.context.LoggingContextConfig;
import de.mtgz.logging.context.LoggingContextConstants;
import de.mtgz.logging.context.LoggingContextService;

import de.mtgz.logging.context.MapConfigSource;
import de.mtgz.logging.correlation.CorrelationIdConstants;
import de.mtgz.logging.trace.TraceContext;
import de.mtgz.logging.trace.TraceContextExtractor;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoggingRequestFilterTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_alle_pflichtfelder_und_correlationId_setzen() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      UriInfo uriInfo = mock(UriInfo.class);
      when(requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME)).thenReturn("corrId_123");
      when(requestContext.getMethod()).thenReturn("GET");
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getPath()).thenReturn("/iphw");

      Config config = ConfigProviderResolver.instance()
         .getBuilder()
         .withSources(new MapConfigSource(Map.of(
            LoggingContextConstants.SERVICE_NAME, "iphw",
            LoggingContextConstants.PROFILE_ENV, "test")))
         .build();
      LoggingContextConfig contextConfig = new LoggingContextConfig(config);
      TraceContextExtractor extractor = mock(TraceContextExtractor.class);
      when(extractor.extract()).thenReturn(new TraceContext("trace", "span"));
      LoggingContextService service = new LoggingContextService(contextConfig, extractor, new IdGeneratorMock("noch_eine_corrId"));

      LoggingRequestFilter filter = new LoggingRequestFilter(service);
      filter.filter(requestContext);

      verify(requestContext).setProperty(CorrelationIdConstants.REQUEST_PROPERTY, "corrId_123");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo("corrId_123");
      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isEqualTo("GET");
      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("/iphw");
      assertThat(MDC.get(LoggingConstants.SERVICE_NAME_KEY)).isEqualTo("iphw");
      assertThat(MDC.get(LoggingConstants.ENVIRONMENT_KEY)).isEqualTo("test");
      assertThat(MDC.get(LoggingConstants.TRACE_ID_KEY)).isEqualTo("trace");
      assertThat(MDC.get(LoggingConstants.SPAN_ID_KEY)).isEqualTo("span");
   }

   @Test
   void soll_alle_pflichtfelder_mit_default_belegen_und_correlationId_generieren_wenn_nicht_vorhanden() {
      ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
      UriInfo uriInfo = mock(UriInfo.class);
      when(requestContext.getHeaderString(CorrelationIdConstants.HEADER_NAME)).thenReturn(null);
      when(requestContext.getMethod()).thenReturn(null);
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getPath()).thenReturn("");

      Config config = ConfigProviderResolver.instance()
         .getBuilder()
         .withSources(new MapConfigSource(Map.of()))
         .build();
      LoggingContextConfig contextConfig = new LoggingContextConfig(config);
      TraceContextExtractor extractor = mock(TraceContextExtractor.class);
      when(extractor.extract()).thenReturn(null);
      LoggingContextService service = new LoggingContextService(contextConfig, extractor, new IdGeneratorMock("default_corrId"));

      LoggingRequestFilter filter = new LoggingRequestFilter(service);
      filter.filter(requestContext);

      verify(requestContext).setProperty(CorrelationIdConstants.REQUEST_PROPERTY, "default_corrId");
      assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isEqualTo("default_corrId");
      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isEqualTo("Unbekannt");
      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("/");
      assertThat(MDC.get(LoggingConstants.TRACE_ID_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.SPAN_ID_KEY)).isNull();
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