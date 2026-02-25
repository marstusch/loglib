package de.drvbund.pruefdienst.logging.exception;

/**
 * Standardisierte Fehlerantwort für APIs.
 */
public record ErrorResponse(String errorId, int status, String message) {

}