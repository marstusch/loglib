package de.drvbund.pruefdienst.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.drvbund.pruefdienst.logging.common.LoggingConstants;
import de.drvbund.pruefdienst.logging.common.UuidGeneratorMock;

class NotFoundExceptionMapperTest {

   @AfterEach
   void tearDown() {
      MDC.clear();
   }

   @Test
   void returnsNotFoundResponse() {
      NotFoundExceptionMapper mapper = new NotFoundExceptionMapper(new UuidGeneratorMock("error-404"));

      Response response = mapper.toResponse(new NotFoundException("nicht gefunden"));

      assertThat(response.getStatus()).isEqualTo(404);
      assertThat(response.getHeaderString(LoggingConstants.ERROR_ID_HEADER)).isEqualTo("error-404");
      ErrorResponse entity = (ErrorResponse) response.getEntity();
      assertThat(entity.errorId()).isEqualTo("error-404");
      assertThat(entity.status()).isEqualTo(404);
      assertThat(entity.message()).isEqualTo("nicht gefunden");
   }
}
