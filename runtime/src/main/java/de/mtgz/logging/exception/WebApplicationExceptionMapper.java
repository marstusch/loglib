package de.mtgz.logging.exception;

import de.mtgz.logging.Logger;
import de.mtgz.logging.LoggerFactory;
import de.mtgz.logging.wrapper.LogLevel;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
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

        return buildResponse(exception, Response.Status.NOT_FOUND.getStatusCode(), LogLevel.ERROR, fallbackMessage);
    }
}