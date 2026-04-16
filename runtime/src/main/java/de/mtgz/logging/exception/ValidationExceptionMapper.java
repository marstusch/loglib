package de.mtgz.logging.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * Wiederverwendbarer Mapper für Validierungsfehler (400).
 *
 * Nicht als globaler Provider registrieren.
 */
public class ValidationExceptionMapper extends BaseExceptionMapper<ConstraintViolationException>
   implements ExceptionMapper<ConstraintViolationException> {

   public ValidationExceptionMapper() {
      super();
   }

   ValidationExceptionMapper(ErrorHandlingService errorHandlingService) {
      super(errorHandlingService);
   }

   @Override
   public Response toResponse(ConstraintViolationException exception) {
      return createErrorResponse(exception, Response.Status.BAD_REQUEST.getStatusCode(),
         "Validierung fehlgeschlagen");
   }
}
