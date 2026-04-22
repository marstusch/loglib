# Sequenzdiagramm: Exception Handling

Bei Fehlern kann der Consumer eigene ExceptionMapper bereitstellen, die auf `BaseExceptionMapper` aufbauen und ein einheitliches Fehlerformat inklusive `X-Error-Id` erzeugen.

```plantuml
@startuml
actor Client
participant "Resource" as Resource
participant "Consumer ExceptionMapper" as Mapper
participant "BaseExceptionMapper" as Base
participant "ErrorHandlingService" as Service
participant "MDC" as MDC
participant "LoggingWrapper" as Logger

Client -> Resource : HTTP Request
Resource -> Resource : throw RuntimeException (Beispiel)
Resource -> Mapper : toResponse(exception)
Mapper -> Base : createErrorResponse(...)
Base -> Service : createErrorContext(...)
Service -> Service : errorId generieren
Service -> MDC : put(errorId)
Service -> Logger : logf(level, exception,\nstatus + errorId + message)
Service -> MDC : remove(errorId)
Service --> Base : ErrorContext(errorId, status, message)
Base --> Mapper : Response(status, ErrorResponse, X-Error-Id)
Mapper --> Client : HTTP Error Response
@enduml
```

`ErrorResponse` enthält `errorId`, `status` und `message`; dieselbe `errorId` wird zusätzlich als Header ausgeliefert. Ohne Consumer-Mapper greift keine globale Exception-Umwandlung durch die Extension.
