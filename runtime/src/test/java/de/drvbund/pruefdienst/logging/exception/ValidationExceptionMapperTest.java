package de.drvbund.pruefdienst.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.drvbund.pruefdienst.logging.common.LoggingConstants;
import de.drvbund.pruefdienst.logging.common.UuidGeneratorMock;

class ValidationExceptionMapperTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void soll_id_status_message_bei_validierungsfehler_liefern() {
      ValidationExceptionMapper mapper = new ValidationExceptionMapper(new UuidGeneratorMock("error-400"));

      Response response = mapper.toResponse(new ConstraintViolationException("ungültig", Collections.emptySet()));

      assertThat(response.getStatus()).isEqualTo(400);
      assertThat(response.getHeaderString(LoggingConstants.ERROR_ID_HEADER)).isEqualTo("error-400");
      ErrorResponse entity = (ErrorResponse) response.getEntity();
      assertThat(entity.errorId()).isEqualTo("error-400");
      assertThat(entity.status()).isEqualTo(400);
      assertThat(entity.message()).isEqualTo("ungültig");
   }
}