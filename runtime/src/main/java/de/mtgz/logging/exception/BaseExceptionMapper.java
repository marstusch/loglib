package de.mtgz.logging.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.MDC;

import de.mtgz.logging.LoggerFactory;
import de.mtgz.logging.wrapper.LogLevel;
import de.mtgz.logging.Logger;
import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGenerator;

/**
 * Basisklasse für das Exception Mapping mit standardisierter Fehlerbehandlung.
 *
 * @param <T> Exception-Typ
 */
abstract class BaseExceptionMapper<T extends Throwable> {

   private Logger logger = LoggerFactory.getLogger(BaseExceptionMapper.class);
   private final UuidGenerator idGenerator;

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   BaseExceptionMapper(Logger logger) {
      this(logger, new UuidGenerator());
   }

   BaseExceptionMapper(Logger logger, UuidGenerator idGenerator) {
      this.logger = logger;
      this.idGenerator = idGenerator;
   }

   Response buildResponse(T exception, int status, LogLevel level, String fallbackMessage) {
      checkHttpMethodAndPath();

      String message = (exception.getMessage() == null || exception.getMessage().isBlank())
         ? fallbackMessage
         : exception.getMessage();

      String errorId = idGenerator.generate();
      MDC.put(LoggingConstants.ERROR_ID_KEY, errorId);
      logger.logf(level, exception, "status=%d errorId=%s ursache=%s", status, errorId, message);

      // nach dem Loggen muss der MDC von der ErrorId gesäubert werden, damit diese im selben Kontext immer weiter gereicht wird
      MDC.remove(LoggingConstants.ERROR_ID_KEY);

      return Response.status(status)
         .header(LoggingConstants.ERROR_ID_HEADER, errorId)
         .entity(new ErrorResponse(errorId, status, message))
         .build();
   }

   private void checkHttpMethodAndPath() {
      if (MDC.get(LoggingConstants.HTTP_METHOD_KEY) == null && request != null) {
         MDC.put(LoggingConstants.HTTP_METHOD_KEY, request.getMethod());
      }
      if (MDC.get(LoggingConstants.HTTP_PATH_KEY) == null && uriInfo != null) {
         MDC.put(LoggingConstants.HTTP_PATH_KEY, uriInfo.getPath());
      }
   }
}