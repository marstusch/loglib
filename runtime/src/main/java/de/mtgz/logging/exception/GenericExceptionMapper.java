package de.mtgz.logging.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import de.mtgz.logging.wrapper.LogLevel;

/**
 * Wiederverwendbarer Mapper für unerwartete Fehler (500).
 *
 * Nicht als globaler Provider registrieren.
 */
public class GenericExceptionMapper extends BaseExceptionMapper<Throwable> implements ExceptionMapper<Throwable> {

   public GenericExceptionMapper() {
      super();
   }

   GenericExceptionMapper(ErrorHandlingService errorHandlingService) {
      super(errorHandlingService);
   }

   @Override
   public Response toResponse(Throwable exception) {
      if (exception instanceof WebApplicationException webApplicationException
         && webApplicationException.getResponse() != null) {
         return webApplicationException.getResponse();
      }

      return createErrorResponse(exception, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), LogLevel.ERROR,
         "Interner Server-Fehler");
   }
}
