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
class GenericExceptionMapper
class ValidationExceptionMapper
class WebApplicationExceptionMapper
class NotFoundExceptionMapper
class ErrorResponse
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

BaseExceptionMapper --> ErrorResponse : builds
GenericExceptionMapper --|> BaseExceptionMapper
ValidationExceptionMapper --|> BaseExceptionMapper
WebApplicationExceptionMapper --|> BaseExceptionMapper
NotFoundExceptionMapper --|> BaseExceptionMapper

LoggingExtensionProcessor ..> LoggerProducer : AdditionalBean
LoggingExtensionProcessor ..> LoggingRequestFilter : AdditionalBean
LoggingExtensionProcessor ..> LoggingResponseFilter : AdditionalBean
LoggingExtensionProcessor ..> CorrelationIdClientRequestFilter : AdditionalBean
LoggingExtensionProcessor ..> GenericExceptionMapper : AdditionalBean
LoggingExtensionProcessor ..> ValidationExceptionMapper : AdditionalBean
LoggingExtensionProcessor ..> WebApplicationExceptionMapper : AdditionalBean

LoggingDefaultsConfigSourceProvider ..> "MicroProfile ConfigSource"
@enduml
```

Hinweis: Es werden bewusst nur Kernbeziehungen gezeigt; Utility-/Security-Klassen sind ausgelassen.
