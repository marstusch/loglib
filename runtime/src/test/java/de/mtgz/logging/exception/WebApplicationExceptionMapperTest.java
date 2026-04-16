package de.mtgz.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.mtgz.logging.Logger;
import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGeneratorMock;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

class WebApplicationExceptionMapperTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_message_der_exception_uebernehmen_wenn_vorhanden() {
      Logger logger = mock(Logger.class);
      WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper(
         new ErrorHandlingService(logger, new UuidGeneratorMock("error-500")));

      Response response = mapper.toResponse(new WebApplicationException("kaputt", Response.status(500).build()));

      assertThat(response.getStatus()).isEqualTo(500);
      assertThat(response.getHeaderString(LoggingConstants.ERROR_ID_HEADER)).isEqualTo("error-500");
      ErrorResponse entity = (ErrorResponse) response.getEntity();
      assertThat(entity.message()).isEqualTo("kaputt");
      assertThat(entity.status()).isEqualTo(500);
   }

   @Test
   void soll_fallbackmessage_resource_nicht_gefunden_nutzen_wenn_status_404_und_message_blank() {
      Logger logger = mock(Logger.class);
      WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper(
         new ErrorHandlingService(logger, new UuidGeneratorMock("error-404")));

      Response response = mapper.toResponse(new WebApplicationException(" ", Response.status(404).build()));

      ErrorResponse entity = (ErrorResponse) response.getEntity();
      assertThat(entity.message()).isEqualTo("Resource nicht gefunden");
      assertThat(entity.status()).isEqualTo(404);
      assertThat(entity.errorId()).isEqualTo("error-404");
   }

   @Test
   void soll_fallbackmessage_request_fehlgeschlagen_nutzen_wenn_status_nicht_404_und_message_null() {
      Logger logger = mock(Logger.class);
      WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper(
         new ErrorHandlingService(logger, new UuidGeneratorMock("error-500")));

      Response response = mapper.toResponse(new WebApplicationException((String) null, Response.status(500).build()));

      ErrorResponse entity = (ErrorResponse) response.getEntity();
      assertThat(entity.message()).isEqualTo("Request fehlgeschlagen");
      assertThat(entity.status()).isEqualTo(500);
      assertThat(entity.errorId()).isEqualTo("error-500");
   }
}
