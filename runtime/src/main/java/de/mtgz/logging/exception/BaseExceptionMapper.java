package de.mtgz.logging.exception;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.wrapper.LogLevel;

/**
 * Basisklasse für ExceptionMapper mit standardisierter ErrorId- und Logging-Behandlung.
 *
 * @param <T> Exception-Typ
 */
public abstract class BaseExceptionMapper<T extends Throwable> {

   @Inject
   ErrorHandlingService errorHandlingService;

   @Context
   Request request;

   @Context
   UriInfo uriInfo;

   protected BaseExceptionMapper() {
   }

   protected BaseExceptionMapper(ErrorHandlingService errorHandlingService) {
      this.errorHandlingService = errorHandlingService;
   }

   protected Response createErrorResponse(T exception, int status, String fallbackMessage) {
      return createErrorResponse(exception, status, LogLevel.ERROR, fallbackMessage);
   }

   protected Response createErrorResponse(T exception, int status, LogLevel level, String fallbackMessage) {
      ErrorContext errorContext = errorHandlingService.createErrorContext(
         exception,
         status,
         fallbackMessage,
         level,
         request,
         uriInfo);

      return Response.status(errorContext.status())
         .header(LoggingConstants.ERROR_ID_HEADER, errorContext.errorId())
         .entity(new ErrorResponse(errorContext.errorId(), errorContext.status(), errorContext.message()))
         .build();
   }

   protected Response erstelleFehlerantwort(T exception, int status, String fallbackMessage) {
      return createErrorResponse(exception, status, fallbackMessage);
   }

   protected Response erstelleFehlerantwort(T exception, int status, LogLevel level, String fallbackMessage) {
      return createErrorResponse(exception, status, level, fallbackMessage);
   }
}
