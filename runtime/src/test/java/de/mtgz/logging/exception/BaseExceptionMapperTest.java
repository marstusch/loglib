package de.mtgz.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.wrapper.LogLevel;

class BaseExceptionMapperTest {

   @Test
   void soll_response_mit_status_errorresponse_und_x_error_id_erzeugen() {
      ErrorHandlingService errorHandlingService = mock(ErrorHandlingService.class);
      TestMapper mapper = new TestMapper(errorHandlingService);
      RuntimeException exception = new RuntimeException("kaputt");
      when(errorHandlingService.createErrorContext(exception, 400, "Fallback", LogLevel.ERROR, null, null))
         .thenReturn(new ErrorContext("error-123", 400, "ungültig"));

      Response response = mapper.toResponse(exception);

      assertThat(response.getStatus()).isEqualTo(400);
      assertThat(response.getHeaderString(LoggingConstants.ERROR_ID_HEADER)).isEqualTo("error-123");
      ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
      assertThat(errorResponse.errorId()).isEqualTo("error-123");
      assertThat(errorResponse.status()).isEqualTo(400);
      assertThat(errorResponse.message()).isEqualTo("ungültig");
      verify(errorHandlingService).createErrorContext(exception, 400, "Fallback", LogLevel.ERROR, null, null);
   }

   private static class TestMapper extends BaseExceptionMapper<RuntimeException> {

      private TestMapper(ErrorHandlingService errorHandlingService) {
         super(errorHandlingService);
      }

      private Response toResponse(RuntimeException exception) {
         return createErrorResponse(exception, 400, "Fallback");
      }
   }
}
