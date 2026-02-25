package de.drvbund.pruefdienst.logging.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

/**
 * Extrahiert Trace- und Span-IDs aus dem aktuellen OpenTelemetry-Kontext.
 */
public class TraceContextExtractor {

   /**
    * Ermittelt den aktuellen TraceContext.
    *
    * @return TraceContext oder {@code null}, wenn kein aktiver Kontext vorhanden ist
    */
   public TraceContext extract() {
      SpanContext spanContext = Span.current().getSpanContext();
      if (spanContext == null || !spanContext.isValid()) {
         return null;
      }
      return new TraceContext(spanContext.getTraceId(), spanContext.getSpanId());
   }
}