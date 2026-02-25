package de.drvbund.pruefdienst.logging.trace;

/**
 * Record für Trace- und Span-IDs.
 */
public record TraceContext(String traceId, String spanId) {

}
