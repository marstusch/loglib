# Sequenzdiagramm: Exception Handling

Bei Fehlern erzeugen die ExceptionMapper ein einheitliches Fehlerformat und setzen den Header `X-Error-Id`.

```plantuml
@startuml
actor Client
participant "Resource" as Resource
participant "ExceptionMapper\n(Generic/Validation/WebApp/NotFound)" as Mapper
participant "BaseExceptionMapper" as Base
participant "MDC" as MDC
participant "LoggingWrapper" as Logger

Client -> Resource : HTTP Request
Resource -> Resource : throw RuntimeException / ConstraintViolationException
Resource -> Mapper : toResponse(exception)
Mapper -> Base : buildResponse(...)
Base -> Base : errorId generieren
Base -> MDC : put(errorId)
Base -> Logger : logf(level, exception,\nstatus + errorId + message)
Base -> MDC : remove(errorId)
Base --> Mapper : Response(status, ErrorResponse, X-Error-Id)
Mapper --> Client : HTTP Error Response
@enduml
```

`ErrorResponse` enthält `errorId`, `status` und `message`; dieselbe `errorId` wird zusätzlich als Header ausgeliefert.
