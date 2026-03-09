# Sequenzdiagramm: Happy Path Request

Happy Path für einen eingehenden HTTP-Request inklusive MDC-Befüllung, Logger-Nutzung und Cleanup im Response-Filter.

```plantuml
@startuml
actor Client
participant "JAX-RS" as Jaxrs
participant "LoggingRequestFilter" as Req
participant "LoggingContextService" as Ctx
participant "Resource" as Resource
participant "LoggerProducer" as Producer
participant "LoggingWrapper\n(JBoss Logger)" as Wrapper
participant "LoggingResponseFilter" as Resp

Client -> Jaxrs : GET /resource\n(X-Correlation-Id optional)
Jaxrs -> Req : filter(request)
Req -> Ctx : resolveCorrelationId(header)
Ctx --> Req : correlationId
Req -> Ctx : setzePflichtfelder(request, correlationId)
note right of Ctx
MDC keys:
- correlationId
- http.method
- http.path
- service.name
- environment
- traceId/spanId (falls vorhanden)
end note

Jaxrs -> Resource : invoke()
Resource -> Producer : @Inject Logger
Producer --> Resource : Logger instance
Resource -> Wrapper : info()/debug()/...
Wrapper --> Resource : writes log event

Jaxrs -> Resp : filter(request, response)
Resp -> Ctx : bereinigeMDC()
Resp -> Client : response + X-Correlation-Id
@enduml
```

Ergebnis: Logs im Request enthalten konsistente MDC-Werte, nach dem Response werden die Felder aufgeräumt.
