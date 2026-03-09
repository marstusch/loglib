# Sequenzdiagramm: Outbound Propagation (Fanout/Call)

Das Diagramm zeigt, wie eine eingehende CorrelationId über den RestClient in Downstream-Calls propagiert wird.

```plantuml
@startuml
actor Client
participant "Inbound RequestFilter" as InReq
participant "MDC" as MDC
participant "Resource" as Resource
participant "RestClient" as RestClient
participant "CorrelationIdClientRequestFilter" as OutReq
participant "Downstream Service" as Downstream

Client -> InReq : HTTP Request\n(X-Correlation-Id = corr-123 oder leer)
InReq -> MDC : put(correlationId)

InReq -> Resource : Request mit gesetzter CorrelationId
Resource -> RestClient : call downstream
RestClient -> OutReq : filter(clientRequest)
OutReq -> MDC : read correlationId (fallback)
OutReq -> RestClient : set header X-Correlation-Id
RestClient -> Downstream : HTTP Request + X-Correlation-Id
Downstream --> Resource : response
Resource --> Client : response + gleiche CorrelationId
@enduml
```

Damit bleibt die gleiche CorrelationId über Inbound, interne Verarbeitung und Outbound-Aufrufe erhalten.
