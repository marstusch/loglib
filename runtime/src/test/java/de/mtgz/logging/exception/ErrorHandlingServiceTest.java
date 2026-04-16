package de.mtgz.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.mtgz.logging.Logger;
import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGeneratorMock;
import de.mtgz.logging.wrapper.LogLevel;

class ErrorHandlingServiceTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_errorid_erzeugen_und_errorcontext_befuellen() {
      Logger logger = mock(Logger.class);
      ErrorHandlingService service = new ErrorHandlingService(logger, new UuidGeneratorMock("error-4711"));

      ErrorContext errorContext = service.createErrorContext(
         new RuntimeException("kaputt"),
         500,
         "Fallback",
         null,
         null);

      assertThat(errorContext.errorId()).isEqualTo("error-4711");
      assertThat(errorContext.status()).isEqualTo(500);
      assertThat(errorContext.message()).isEqualTo("kaputt");
      assertThat(MDC.get(LoggingConstants.ERROR_ID_KEY)).isNull();
   }

   @Test
   void soll_standardisiertes_fehlerlog_mit_errorid_und_status_schreiben() {
      Logger logger = mock(Logger.class);
      ErrorHandlingService service = new ErrorHandlingService(logger, new UuidGeneratorMock("error-log"));
      RuntimeException exception = new RuntimeException("boom");

      service.createErrorContext(exception, 500, "Fallback", LogLevel.ERROR, null, null);

      verify(logger).logf(eq(LogLevel.ERROR), eq(exception), eq("status=%d errorId=%s ursache=%s"), eq(500), eq("error-log"),
         eq("boom"));
   }

   @Test
   void soll_http_method_und_http_path_aus_request_und_uriinfo_in_mdc_setzen() {
      Logger logger = mock(Logger.class);
      ErrorHandlingService service = new ErrorHandlingService(logger, new UuidGeneratorMock("error-ctx"));
      Request request = mock(Request.class);
      UriInfo uriInfo = mock(UriInfo.class);
      when(request.getMethod()).thenReturn("GET");
      when(uriInfo.getPath()).thenReturn("/api/test");
      MDC.put(LoggingConstants.TRACE_ID_KEY, "trace-1");
      MDC.put(LoggingConstants.SPAN_ID_KEY, "span-1");
      MDC.put("correlationId", "corr-1");

      service.createErrorContext(new RuntimeException("boom"), 500, "Fallback", request, uriInfo);

      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isEqualTo("GET");
      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("/api/test");
      assertThat(MDC.get(LoggingConstants.TRACE_ID_KEY)).isEqualTo("trace-1");
      assertThat(MDC.get(LoggingConstants.SPAN_ID_KEY)).isEqualTo("span-1");
      assertThat(MDC.get("correlationId")).isEqualTo("corr-1");
      verify(logger).logf(any(), any(Throwable.class), any(), any(), any(), any());
   }
}
