# Logging-Library

Logging ist eine in Java 21 geschriebene Library für Quarkus-Microservices. Sie stellt wiederverwendbare Logging-Funktionen bereit, damit Entwickler diese nicht in jedem Service selbst implementieren müssen. Schwerpunkte sind CorrelationId-Propagation, standardisierte MDC-Felder sowie ein konsistentes Exception-Handling inklusive ErrorId.

## Setup & Entwicklung

### Voraussetzungen

- Java 21 (JDK)
- Maven 3.9+

### Build & Tests

```bash
mvn clean install
```

### Entwicklungshinweise

- Alle Logging-Provider sind als JAX-RS-Provider registriert und wirken automatisch in Quarkus-Services.
- Die Library setzt MDC-Felder für CorrelationId, HTTP-Metadaten sowie (falls aktiv) Trace/Span-IDs.
- Fehler werden über globale ExceptionMapper konsistent behandelt und liefern eine ErrorId im Response-Header `X-Error-Id`.

## Verwendung als Maven-Dependency

Füge die Dependency im Zielprojekt (z. B. Quarkus-Microservice) ein:

```xml
<dependency>
   <groupId>de.mtgz.logging</groupId>
   <artifactId>logging</artifactId>
   <version>0.1-SNAPSHOT</version>
</dependency>
```

## Konfiguration des Loggings in anderen Quarkus-Projekten
Damit in deinem Quarkus-Projekt auch richtig geloggt wird, muss die `application.properties` angepasst werden. Füge dafür folgende Zeilen hinzu:
```properties
quarkus.log.console.json=true
quarkus.log.console.json.pretty-print=true
quarkus.log.console.level=INFO
quarkus.log.console.json.additional-field.service.value=bitte_Service-Namen_einsetzen
quarkus.log.console.json.additional-field.environment.value=${quarkus.profile}

quarkus.otel.enabled=true
quarkus.otel.traces.enabled=true
quarkus.otel.metrics.enabled=true
quarkus.micrometer.export.prometheus.enabled=true
```

Damit diese Konfiguration ausgeführt wird, musst du lokal deine Applikation mit folgendem Befehl starten (in der OCP geschieht das automatisch):
```bash
 ./mvnw quarkus:dev -Dquarkus.profile=prod
```

### Human-Readable Logs
Für die Entwicklung empfiehlt es sich auf Human-Readable Logs zurückzugreifen. Dafür muss die Datei `application-dev.properties` im `resources` Verzeichnis des Projekt angelegt werden:
````properties
quarkus.log.console.json=false
quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p %m service=%X{service} env=%X{environment} host=%h traceId=%X{traceId} spanId=%X{spanId} corrId=%X{correlationId} http.method=%X{http.method} http.path=%X{http.path}%n
quarkus.log.console.level=DEBUG
````
Diese Konfiguration wird ausgeführt, wenn die Applikation normal im Dev-Modus gestartet wird:
```bash
 ./mvnw quarkus:dev
```
## Verwendung des Loggers
Die `LoggerFactory` liefert euch eine Implentierung des `Logger`-Interfaces zurück. Die Verwendung in deiner Klasse erfolgt so:
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
