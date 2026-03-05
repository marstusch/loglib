# Logging

Logging ist eine in Java 21 geschriebene Quarkus Extension für andere Quarkus Microservices. Sie stellt wiederverwendbare Logging-Funktionen bereit, damit Entwickler diese nicht in jedem Service selbst implementieren müssen. Schwerpunkte sind CorrelationId-Propagation, standardisierte MDC-Felder sowie ein konsistentes Exception-Handling.

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
# Logging Konfiguration PROD (und andere Umgebungen != DEV)
# =========================================================
%prod.quarkus.log.level=INFO
%prod.quarkus.log.console.level=INFO
%prod.quarkus.log.console.json.enabled=true
%prod.quarkus.log.console.json.pretty-print=true
```

Wenn ihr eine oder mehrere Properties ändern wollt, dann überschreibt den Eintrag in eurer eigenen `application.properties`.

### Logging mit unterschiedlichen Profilen
In der Standard-Konfiguration finden sich zwei Profile:
* DEV
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

### Entwicklungshinweise

- Alle Logging-Provider sind als JAX-RS-Provider registriert und wirken automatisch in Quarkus Services (siehe: `de.mtgz.logging.extension.deployment.LoggingExtensionProcessor`).
- Die Library setzt MDC-Felder für CorrelationId, HTTP-Metadaten sowie Trace/Span-IDs.
- Fehler werden über globale ExceptionMapper konsistent behandelt und liefern eine ErrorId im Response-Header.