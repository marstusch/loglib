package de.mtgz.logging.exception;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.MDC;

import de.mtgz.logging.Logger;
import de.mtgz.logging.LoggerFactory;
import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.common.UuidGenerator;
import de.mtgz.logging.wrapper.LogLevel;

/**
 * Zentraler Service für ErrorId-Erzeugung und standardisiertes Exception-Logging.
 */
@ApplicationScoped
public class ErrorHandlingService {

   private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingService.class);

   private final Logger log;
   private final UuidGenerator idGenerator;

   public ErrorHandlingService() {
      this(logger, new UuidGenerator());
   }

   ErrorHandlingService(Logger log, UuidGenerator idGenerator) {
      this.log = log;
      this.idGenerator = idGenerator;
   }

   public ErrorContext createErrorContext(Throwable exception,
      int status,
      String fallbackMessage,
      LogLevel level,
      Request request,
      UriInfo uriInfo) {
      ensureHttpMethodAndPathInMdc(request, uriInfo);

      String message = resolveMessage(exception, fallbackMessage);
      String errorId = idGenerator.generate();

      MDC.put(LoggingConstants.ERROR_ID_KEY, errorId);
      log.logf(level, exception, "status=%d errorId=%s ursache=%s", status, errorId, message);
      MDC.remove(LoggingConstants.ERROR_ID_KEY);

      return new ErrorContext(errorId, status, message);
   }

   public ErrorContext createErrorContext(Throwable exception,
      int status,
      String fallbackMessage,
      Request request,
      UriInfo uriInfo) {
      return createErrorContext(exception, status, fallbackMessage, LogLevel.ERROR, request, uriInfo);
   }

   private void ensureHttpMethodAndPathInMdc(Request request, UriInfo uriInfo) {
      if (MDC.get(LoggingConstants.HTTP_METHOD_KEY) == null && request != null) {
         MDC.put(LoggingConstants.HTTP_METHOD_KEY, request.getMethod());
      }
      if (MDC.get(LoggingConstants.HTTP_PATH_KEY) == null && uriInfo != null) {
         MDC.put(LoggingConstants.HTTP_PATH_KEY, uriInfo.getPath());
      }
   }

   private String resolveMessage(Throwable exception, String fallbackMessage) {
      if (exception.getMessage() == null || exception.getMessage().isBlank()) {
         return fallbackMessage;
      }
      return exception.getMessage();
   }
}
