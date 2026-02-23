# quarkus-mtgz-logging

`quarkus-mtgz-logging` ist jetzt eine **echte Quarkus Extension** mit Runtime- und Deployment-Modul.

## Was war schon vorhanden (Analyse)

Bereits implementiert und übernommen:
- JAX-RS Request/Response Filter für `correlationId`, `http.method`, `http.path`.
- Rest-Client Request Filter zur CorrelationId-Propagation.
- MDC-Befüllung inkl. Trace/Span-Auslesen über OpenTelemetry API.
- ExceptionMapper (`GenericExceptionMapper`, `NotFoundExceptionMapper`, `ValidationExceptionMapper`).
- Logging Wrapper/Masking/Hashing Utilitys.

Neu als Extension ergänzt:
- automatische Default-Konfiguration (JSON Logging, OTel, additional fields service/environment),
- automatische Provider/Filter-Aktivierung ohne `quarkus.index-dependency.*` im Consumer,
- transitive Quarkus-Dependencies über ein einziges Consumer-Artifact.

## Consumer Usage (nur 1 Dependency)

```xml
<dependency>
  <groupId>de.mtgz.logging</groupId>
  <artifactId>quarkus-mtgz-logging</artifactId>
  <version>0.1-SNAPSHOT</version>
</dependency>
```

Keine weiteren Logging/OTel/Validation/Micrometer Dependencies nötig.

## Default-Verhalten der Extension

Die Extension setzt nur Defaults (überschreibbar durch Consumer):

```properties
quarkus.log.console.json=true
quarkus.log.console.json.additional-field.service.value=${quarkus.application.name:unknown-service}
quarkus.log.console.json.additional-field.environment.value=${quarkus.profile:prod}
quarkus.otel.enabled=true
quarkus.otel.traces.enabled=true
```

### JSON-Logfelder

Folgende Felder sind damit standardmäßig verfügbar:
- `service`, `environment` (via JSON additional fields),
- `correlationId`, `http.method`, `http.path` (via Request-Filter + MDC),
- `traceId`, `spanId` (OTel + MDC-Befüllung).

## Optionale Overrides im Consumer

```properties
quarkus.log.console.json.additional-field.service.value=my-service
quarkus.log.console.json.additional-field.environment.value=staging
```

## Projektstruktur

- `runtime/`: Laufzeitcode der Extension (Filter, Mapper, MDC, Utilities, Extension-Metadaten)
- `deployment/`: BuildSteps/Processor für Default-Config und automatische Registrierung

## Build

```bash
mvn clean verify
```
