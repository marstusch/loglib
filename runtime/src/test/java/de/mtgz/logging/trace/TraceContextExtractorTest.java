package de.mtgz.logging.trace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;

class TraceContextExtractorTest {

   @Test
   void soll_null_liefern_wenn_kein_valider_spancontext_aktiv_ist() {
      TraceContextExtractor extractor = new TraceContextExtractor();

      TraceContext traceContext = extractor.extract();

      assertThat(traceContext).isNull();
   }

   @Test
   void soll_tracecontext_liefern_wenn_valider_span_aktiv_ist() {
      String traceId = "0123456789abcdef0123456789abcdef";
      String spanId = "0123456789abcdef";
      SpanContext spanContext = SpanContext.create(traceId, spanId, TraceFlags.getSampled(), TraceState.getDefault());

      TraceContextExtractor extractor = new TraceContextExtractor();
      TraceContext traceContext;
      try (var scope = Span.wrap(spanContext).makeCurrent()) {
         traceContext = extractor.extract();
      }

      assertThat(traceContext).isNotNull();
      assertThat(traceContext.traceId()).isEqualTo(traceId);
      assertThat(traceContext.spanId()).isEqualTo(spanId);
   }
}
