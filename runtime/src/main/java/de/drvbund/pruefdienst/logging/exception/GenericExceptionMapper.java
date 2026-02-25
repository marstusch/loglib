package de.drvbund.pruefdienst.logging.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import de.drvbund.pruefdienst.logging.wrapper.LogLevel;
import de.drvbund.pruefdienst.logging.Logger;
import de.drvbund.pruefdienst.logging.LoggerFactory;
import de.drvbund.pruefdienst.logging.common.UuidGenerator;

/**
 * ExceptionMapper für unerwartete Fehler (500).
 */
@Provider
public class GenericExceptionMapper extends BaseExceptionMapper<Throwable> implements ExceptionMapper<Throwable> {

   private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

   /**
    * Erstellt einen Mapper mit Standard-Logger.
    */
   public GenericExceptionMapper() {
      super(logger);
   }

   GenericExceptionMapper(UuidGenerator idGenerator) {
      super(logger, idGenerator);
   }

   /**
    * Erstellt die Fehlerantwort für unerwartete Fehler.
    *
    * @param exception Exception
    *
    * @return Response mit Fehlerdetails
    */
   @Override
   public Response toResponse(Throwable exception) {
      return buildResponse(exception, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), LogLevel.ERROR,
         "Interner Server-Fehler");
   }
}
