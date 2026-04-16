package de.mtgz.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.mtgz.logging.Logger;
import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGeneratorMock;

class GenericExceptionMapperTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_id_status_message_bei_random_exception_liefern() {
      Logger logger = mock(Logger.class);
      ErrorHandlingService errorHandlingService = new ErrorHandlingService(logger, new UuidGeneratorMock("error-123"));
      GenericExceptionMapper mapper = new GenericExceptionMapper(errorHandlingService);
      Request request = mock(Request.class);
      UriInfo uriInfo = mock(UriInfo.class);
      when(request.getMethod()).thenReturn("POST");
      when(uriInfo.getPath()).thenReturn("/fail");
      mapper.request = request;
      mapper.uriInfo = uriInfo;

      Response response = mapper.toResponse(new RuntimeException());

      assertThat(response.getStatus()).isEqualTo(500);
      assertThat(response.getHeaderString(LoggingConstants.ERROR_ID_HEADER)).isEqualTo("error-123");
      ErrorResponse entity = (ErrorResponse) response.getEntity();
      assertThat(entity.errorId()).isEqualTo("error-123");
      assertThat(entity.status()).isEqualTo(500);
      assertThat(entity.message()).isEqualTo("Interner Server-Fehler");
      assertThat(MDC.get(LoggingConstants.ERROR_ID_KEY)).isNull();
      assertThat(MDC.get(LoggingConstants.HTTP_METHOD_KEY)).isEqualTo("POST");
      assertThat(MDC.get(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("/fail");
   }

   @Test
   void soll_webapplicationexception_response_unveraendert_durchreichen() {
      Logger logger = mock(Logger.class);
      GenericExceptionMapper mapper = new GenericExceptionMapper(
         new ErrorHandlingService(logger, new UuidGeneratorMock("error-ignored")));
      Response originalResponse = Response.status(403).header("X-Test", "value").entity("forbidden").build();

      Response response = mapper.toResponse(new WebApplicationException("forbidden", originalResponse));

      assertThat(response).isSameAs(originalResponse);
      assertThat(response.getStatus()).isEqualTo(403);
      assertThat(response.getHeaderString(LoggingConstants.ERROR_ID_HEADER)).isNull();
   }
}
