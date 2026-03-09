# Architektur-Überblick

Diese Seite fasst die zentralen Laufzeit-Flows der Extension zusammen: Logger-Injection, Inbound/Outbound-CorrelationId, MDC-Befüllung und Exception-Handling.

```plantuml
@startuml
skinparam componentStyle rectangle

actor "HTTP Client" as Client
component "JAX-RS Runtime" as Jaxrs
component "LoggingRequestFilter" as Req
component "LoggingContextService" as Ctx
component "Resource + @Inject Logger" as Res
component "LoggerProducer" as Prod
component "LoggingWrapper\n(JBoss Logger)" as Wrapper
component "CorrelationIdClientRequestFilter" as Out
component "ExceptionMapper" as Ex
component "LoggingResponseFilter" as Resp

Client --> Jaxrs : HTTP Request
Jaxrs --> Req : filter(request)
Req --> Ctx : resolveCorrelationId(),\nsetzePflichtfelder()
Jaxrs --> Res : invoke resource
Res --> Prod : @Inject Logger
Prod --> Wrapper : LoggerFactory.getLogger()
Res --> Wrapper : info()/error()/...
Res --> Out : RestClient call (optional)
Out --> Client : X-Correlation-Id weitergeben
Res --> Ex : throw Exception (optional)
Jaxrs --> Resp : filter(response)
Resp --> Ctx : bereinigeMDC()
Resp --> Client : HTTP Response + X-Correlation-Id
@enduml
```

Legende: CorrelationId wird für Inbound/Outbound über `X-Correlation-Id` geführt, Fehler über `X-Error-Id`, und strukturierte Felder werden im MDC gehalten.
