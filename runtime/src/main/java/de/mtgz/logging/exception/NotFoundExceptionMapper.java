package de.mtgz.logging.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import de.mtgz.logging.wrapper.LogLevel;
import de.mtgz.logging.Logger;
import de.mtgz.logging.LoggerFactory;
import de.mtgz.logging.common.UuidGenerator;

/**
 * ExceptionMapper für nicht gefundene Ressourcen (404).
 */
@Provider
public class NotFoundExceptionMapper extends BaseExceptionMapper<NotFoundException>
   implements ExceptionMapper<NotFoundException> {

   private static final Logger logger = LoggerFactory.getLogger(NotFoundExceptionMapper.class);

   /**
    * Erstellt einen Mapper mit Standard-Logger.
    */
   public NotFoundExceptionMapper() {
      super(logger);
   }

   NotFoundExceptionMapper(UuidGenerator idGenerator) {
      super(logger, idGenerator);
   }

   /**
    * Erstellt die Fehlerantwort für 404-Fehler.
    *
    * @param exception Exception
    *
    * @return Response mit Fehlerdetails
    */
   @Override
   public Response toResponse(NotFoundException exception) {
      return buildResponse(exception, Response.Status.NOT_FOUND.getStatusCode(), LogLevel.ERROR, "Not Found");
   }
}
