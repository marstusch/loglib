# Logging

Logging ist eine in Java 21 geschriebene Quarkus Extension für andere Quarkus Microservices. Sie stellt wiederverwendbare Logging-Funktionen bereit, damit Entwickler diese nicht in jedem Service selbst implementieren müssen. Schwerpunkte sind CorrelationId-Propagation, standardisierte MDC-Felder sowie ein konsistentes Exception-Handling.

## Inhaltsverzeichnis

- [Verwendung als Maven-Dependency](#verwendung-als-maven-dependency)
- [Verwendung des Loggers](#verwendung-des-loggers)
- [Überschreiben der Standard-Logging-Konfiguration in Quarkus Projekten](#überschreiben-der-standard-logging-konfiguration-in-quarkus-projekten)
  - [Logging mit unterschiedlichen Profilen](#logging-mit-unterschiedlichen-profilen)
- [Setup & Entwicklung](#setup--entwicklung)
  - [Voraussetzungen](#voraussetzungen)
  - [Build & Tests](#build--tests)
  - [Consumer-Integrationstests der Extension ausführen](#consumer-integrationstests-der-extension-ausführen)
  - [Projektstruktur (Module)](#projektstruktur-module)
  - [Package-Übersicht (runtime/deployment)](#package-übersicht-runtimedeployment)
  - [Entwicklungshinweise](#entwicklungshinweise)
- [Architektur](#architektur)
  - [Architektur-Überblick](docs/architecture/overview.md)
  - [Klassendiagramm (High-Level)](docs/architecture/class-diagram.md)
  - [Sequenzdiagramm: Happy Path Request](docs/architecture/sequence-happy-path.md)
  - [Sequenzdiagramm: Outbound Propagation](docs/architecture/sequence-outbound-propagation.md)
  - [Sequenzdiagramm: Exception Handling](docs/architecture/sequence-exception-handling.md)
  - [Komponentendiagramm / Moduldiagramm](docs/architecture/module-diagram.md)
  - [MDC-Datenfluss](docs/architecture/mdc-dataflow.md)

## Verwendung als Maven-Dependency

Füge die Dependency im Zielprojekt (Quarkus Microservice) ein:

```xml
<dependency>
   <groupId>de.drvbund.pruefdienst.logging</groupId>
   <artifactId>logging-extension</artifactId>
   <version>0.1-SNAPSHOT</version>
</dependency>
```

## Verwendung des Loggers

Die Verwendung in deiner Klasse erfolgt so:
````java
@Inject
Logger logger;
````
Solltest du den Logger nicht in einer CDI-Bean verwenden, dann erfolgt die Deklaration in deiner Klasse wie folgt:
````java
private Logger logger = LoggerFactory.getLogger(DeineKlasse.class);
````

Log-Statements werden bspw. so erzeugt:
````java
// ohne Formatter
logger.info("Abfrage für UserId " + userId + " war erfolgreich...");

// mit Formatter
logger.infof("Abfrage für UserId %s war erfolgreich...", userId);
````

## Überschreiben der Standard-Logging-Konfiguration in Quarkus Projekten
Die Quarkus Extension bringt bereits eine standardisierte Konfiguration für das Logging mit. Diese kann in jeder `application.properties` überschrieben werden. Das ist aber nicht zwingend notwendig.<br>
Standard-Konfiguration:
```properties
# =========================================================
# Logging Konfiguration allgemein
# =========================================================

# OTel
quarkus.otel.enabled=true
quarkus.otel.traces.enabled=true
quarkus.otel.metrics.enabled=true

# =========================================================
# Logging Konfiguration DEV
# =========================================================
%dev.quarkus.log.level=DEBUG
%dev.quarkus.log.console.level=DEBUG
%dev.quarkus.log.console.json.enabled=false
%dev.quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p service=%X{service.name} env=%X{environment} host=%h traceId=%X{traceId} spanId=%X{spanId} corrId=%X{correlationId} http.method=%X{http.method} http.path=%X{http.path} message=%m %n

# =========================================================
# Logging Konfiguration TEST
# =========================================================
%test.quarkus.log.level=DEBUG
%test.quarkus.log.console.level=DEBUG
%test.quarkus.log.console.json.enabled=false
%test.quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p service=%X{service.name} env=%X{environment} host=%h traceId=%X{traceId} spanId=%X{spanId} corrId=%X{correlationId} http.method=%X{http.method} http.path=%X{http.path} message=%m %n

# =========================================================
# Logging Konfiguration PROD (und andere Umgebungen != DEV)
# =========================================================
%prod.quarkus.log.level=INFO
%prod.quarkus.log.console.level=INFO
%prod.quarkus.log.console.json.enabled=true
%prod.quarkus.log.console.json.pretty-print=true
```

Wenn ihr eine oder mehrere Properties ändern wollt, dann überschreibt den Eintrag in eurer eigenen `application.properties`.

### Logging mit unterschiedlichen Profilen
In der Standard-Konfiguration finden sich drei Profile:
* DEV
* TEST
* PROD

#### Profil DEV mit Human-Readable Logs
Für die Entwicklung empfiehlt es sich auf Human-Readable Logs zurückzugreifen.<br>
Diese Konfiguration wird ausgeführt, wenn die Applikation normal im Dev-Modus gestartet wird:
```bash
 ./mvnw quarkus:dev
```

#### Profil PROD mit JSON-Logging für alle anderen Umgebungen
Damit diese Konfiguration ausgeführt wird, musst du lokal deine Applikation mit folgendem Befehl starten (in der OCP geschieht das automatisch):
```bash
 ./mvnw quarkus:run -Dquarkus.profile=prod
```

## Setup & Entwicklung

### Voraussetzungen

- Java 21 (21rh JDK)
- Maven 3.9+

### Build & Tests

```bash
mvn clean install
```

### Consumer-Integrationstests der Extension ausführen

```bash
mvn clean verify
```

oder gezielt nur das Consumer-IT-Modul:

```bash
mvn -pl integration-tests -am verify
```

Die `@QuarkusTest`-Suite im Modul `integration-tests` startet eine kleine Consumer-ähnliche Quarkus-App mit `/it/*` Endpunkten und prüft CorrelationId-Propagation, Deployment-Registrierung, ExceptionMapper und MDC-Cleanup End-to-End.

### Projektstruktur (Module)

```text
loglib/
├─ deployment/
│  └─ src/main/java/de/mtgz/logging/extension/deployment/
│     └─ LoggingExtensionProcessor.java
├─ runtime/
│  ├─ src/main/java/de/mtgz/logging/
│  │  ├─ Logger.java
│  │  ├─ LoggerFactory.java
│  │  ├─ LoggingRequestFilter.java
│  │  ├─ LoggingResponseFilter.java
│  │  ├─ correlation/
│  │  ├─ context/
│  │  ├─ exception/
│  │  ├─ injection/
│  │  ├─ config/
│  │  ├─ trace/
│  │  ├─ wrapper/
│  │  ├─ security/
│  │  └─ common/
│  └─ src/main/resources/META-INF/
│     ├─ quarkus-extension.yaml
│     └─ services/
└─ integration-tests/
   ├─ src/main/java/de/mtgz/logging/it/
   └─ src/test/java/de/mtgz/logging/it/
```

### Package-Übersicht (runtime/deployment)

- `de.mtgz.logging` — Logger API, Factory und zentrale Request/Response-Filter für MDC-Befüllung/Cleanup.
- `de.mtgz.logging.injection` — CDI-Producer für `@Inject Logger`.
- `de.mtgz.logging.wrapper` — Adapter auf JBoss Logger (`LoggingWrapper`, `LogLevel`).
- `de.mtgz.logging.context` — Auflösung von Service/Umgebung und Setzen/Löschen der MDC-Pflichtfelder.
- `de.mtgz.logging.correlation` — CorrelationId Header/MDC Handling, Inbound/Outbound-Filter und Utilities.
- `de.mtgz.logging.exception` — ExceptionMapper, `ErrorResponse` und `X-Error-Id`-Responseaufbau.
- `de.mtgz.logging.trace` — Extraktion von TraceId/SpanId aus OpenTelemetry.
- `de.mtgz.logging.config` — Default-ConfigSource für Logging-/OTel-Defaults je Profil.
- `de.mtgz.logging.security` — Masking/Hashing Utilities für sichere Log-Ausgabe.
- `de.mtgz.logging.common` — Konstante Keys/Header und UUID-Generierung.
- `de.mtgz.logging.extension.deployment` — Quarkus Build Steps (Feature + AdditionalBean-Registrierung).

### Entwicklungshinweise

- Der `LoggingExtensionProcessor` registriert zentrale Runtime-Beans (z. B. `LoggerProducer`, Logging-Filter, CorrelationId-Outbound-Filter und ExceptionMapper) als unremovable Additional Beans.
- `LoggingRequestFilter` setzt CorrelationId, HTTP-Metadaten, Service/Umgebung und Trace/Span in das MDC; `LoggingResponseFilter` schreibt CorrelationId in den Response und bereinigt das MDC.
- `CorrelationIdClientRequestFilter` propagiert die CorrelationId in RestClient-Aufrufen.
- ExceptionMapper liefern ein einheitliches `ErrorResponse`-Payload und den Header `X-Error-Id`.

## Architektur

Die detaillierten Diagramme liegen unter `docs/architecture/`:

- [Architektur-Überblick](docs/architecture/overview.md)
- [Klassendiagramm (High-Level)](docs/architecture/class-diagram.md)
- [Sequenzdiagramm: Happy Path Request](docs/architecture/sequence-happy-path.md)
- [Sequenzdiagramm: Outbound Propagation](docs/architecture/sequence-outbound-propagation.md)
- [Sequenzdiagramm: Exception Handling](docs/architecture/sequence-exception-handling.md)
- [Komponentendiagramm / Moduldiagramm](docs/architecture/module-diagram.md)
- [MDC-Datenfluss](docs/architecture/mdc-dataflow.md)
