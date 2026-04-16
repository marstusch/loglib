package de.mtgz.logging.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import de.mtgz.logging.Logger;
import de.mtgz.logging.LoggerFactory;
import de.mtgz.logging.wrapper.LogLevel;

/**
 * Optionaler Mapper für bewusst geworfene {@link WebApplicationException}.
 *
 * Nicht als globaler Provider registrieren, da sonst Framework-/Routing-Fehler
 * (z. B. 404 bei Nicht-Match) unbeabsichtigt als fachliche ErrorResponse
 * überschrieben werden können.
 */
public class WebApplicationExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<WebApplicationException> {

   private static final Logger logger = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

   public WebApplicationExceptionMapper() {
      super(logger);
   }

   @Override
   public Response toResponse(WebApplicationException exception) {
      int status = exception.getResponse() != null
         ? exception.getResponse().getStatus()
         : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

      String fallbackMessage = status == Response.Status.NOT_FOUND.getStatusCode() ? "Resource nicht gefunden" : "Request fehlgeschlagen";

      return buildResponse(exception, status, LogLevel.ERROR, fallbackMessage);
   }
}
