# Klassendiagramm (High-Level)

Das Diagramm zeigt die wichtigsten Typen und ihre Beziehungen zwischen Logger API, Filtern, CorrelationId-, Exception- und Deployment-Logik.

```plantuml
@startuml
skinparam classAttributeIconSize 0

interface Logger
class LoggerFactory
class LoggingWrapper
class LoggerProducer
class LoggingRequestFilter
class LoggingResponseFilter
class LoggingContextService
class CorrelationIdRequestFilter
class CorrelationIdResponseFilter
class CorrelationIdClientRequestFilter
class CorrelationIdUtil
class BaseExceptionMapper
class ErrorHandlingService
class ErrorContext
class ErrorResponse
class ConsumerExceptionMapper
class LoggingDefaultsConfigSourceProvider
class LoggingExtensionProcessor

LoggerFactory ..> LoggingWrapper : creates
LoggingWrapper ..|> Logger
LoggerProducer ..> LoggerFactory : produces Logger

LoggingRequestFilter --> LoggingContextService
LoggingResponseFilter --> LoggingContextService

CorrelationIdRequestFilter ..> CorrelationIdUtil
CorrelationIdResponseFilter ..> CorrelationIdUtil
CorrelationIdClientRequestFilter ..> CorrelationIdUtil

BaseExceptionMapper --> ErrorHandlingService : uses
BaseExceptionMapper --> ErrorResponse : builds
ErrorHandlingService --> ErrorContext : creates
ConsumerExceptionMapper --|> BaseExceptionMapper

LoggingExtensionProcessor ..> LoggerProducer : AdditionalBean
LoggingExtensionProcessor ..> LoggingRequestFilter : AdditionalBean
LoggingExtensionProcessor ..> LoggingResponseFilter : AdditionalBean
LoggingExtensionProcessor ..> CorrelationIdClientRequestFilter : AdditionalBean

LoggingDefaultsConfigSourceProvider ..> "MicroProfile ConfigSource"
@enduml
```

Hinweis: Es werden bewusst nur Kernbeziehungen gezeigt; Utility-/Security-Klassen sind ausgelassen. Konkrete `ConsumerExceptionMapper` sind Platzhalter für Mapper im jeweiligen Consumer-Service und werden nicht durch die Extension automatisch aktiviert.
