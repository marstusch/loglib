package de.mtgz.logging.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import de.mtgz.logging.wrapper.LogLevel;
import de.mtgz.logging.Logger;
import de.mtgz.logging.LoggerFactory;
import de.mtgz.logging.common.UuidGenerator;

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
      if (exception instanceof WebApplicationException webApplicationException
         && webApplicationException.getResponse() != null) {
         return webApplicationException.getResponse();
      }

      return buildResponse(exception, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), LogLevel.ERROR,
         "Interner Server-Fehler");
   }
}
