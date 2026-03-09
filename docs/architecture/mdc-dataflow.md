# MDC-Datenfluss

Dieses Diagramm fokussiert auf die gesetzten und gelöschten MDC-Keys sowie Header im Request/Response-Lebenszyklus.

```plantuml
@startuml
start
:Inbound Header lesen\nX-Correlation-Id;
if (Header vorhanden?) then (ja)
  :CorrelationId übernehmen;
else (nein)
  :CorrelationId generieren;
endif

:MDC.put(correlationId);
:MDC.put(http.method, http.path);
:MDC.put(service.name, environment);
if (aktiver OTel Span?) then (ja)
  :MDC.put(traceId, spanId);
else (nein)
  :MDC.remove(traceId, spanId);
endif

:Business-Logik + Logger;
:Outbound RestClient setzt\nX-Correlation-Id;

if (Exception?) then (ja)
  :errorId generieren;
  :MDC.put(errorId);
  :Response Header X-Error-Id setzen;
  :MDC.remove(errorId);
endif

:Response Header X-Correlation-Id setzen;
:MDC cleanup\n(correlationId, http.*, service/env, trace/span, errorId);
stop
@enduml
```

Wichtige Header: `X-Correlation-Id`, `X-Error-Id`. Wichtige MDC-Keys: `correlationId`, `http.method`, `http.path`, `service.name`, `environment`, `traceId`, `spanId`, `errorId`.
