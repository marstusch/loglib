package de.drvbund.pruefdienst.logging.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import de.drvbund.pruefdienst.logging.wrapper.LogLevel;
import de.drvbund.pruefdienst.logging.Logger;
import de.drvbund.pruefdienst.logging.LoggerFactory;
import de.drvbund.pruefdienst.logging.common.UuidGenerator;

/**
 * ExceptionMapper für Validierungsfehler (400).
 */
@Provider
public class ValidationExceptionMapper extends BaseExceptionMapper<ConstraintViolationException>
   implements ExceptionMapper<ConstraintViolationException> {

   private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionMapper.class);

   /**
    * Erstellt einen Mapper mit Standard-Logger.
    */
   public ValidationExceptionMapper() {
      super(logger);
   }

   ValidationExceptionMapper(UuidGenerator idGenerator) {
      super(logger, idGenerator);
   }

   /**
    * Erstellt die Fehlerantwort für Validierungsfehler.
    *
    * @param exception Exception
    *
    * @return Response mit Fehlerdetails
    */
   @Override
   public Response toResponse(ConstraintViolationException exception) {
      return buildResponse(exception, Response.Status.BAD_REQUEST.getStatusCode(), LogLevel.ERROR,
         "Validierung fehlgeschlagen");
   }
}
