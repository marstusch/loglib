package de.mtgz.logging.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * Wiederverwendbarer Mapper für nicht gefundene Ressourcen (404).
 *
 * Nicht als globaler Provider registrieren.
 */
public class NotFoundExceptionMapper extends BaseExceptionMapper<NotFoundException>
   implements ExceptionMapper<NotFoundException> {

   public NotFoundExceptionMapper() {
      super();
   }

   NotFoundExceptionMapper(ErrorHandlingService errorHandlingService) {
      super(errorHandlingService);
   }

   @Override
   public Response toResponse(NotFoundException exception) {
      return createErrorResponse(exception, Response.Status.NOT_FOUND.getStatusCode(), "Not Found");
   }
}
