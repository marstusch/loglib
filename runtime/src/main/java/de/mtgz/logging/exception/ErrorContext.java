package de.mtgz.logging.exception;

/**
 * Datenbasis für standardisiertes Fehlerhandling.
 */
public record ErrorContext(String errorId, int status, String message) {

}
