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

**Wichtig:** Hier wird vorert eine Übergangslösung beschrieben, bis die Lib als Quarkus-Extension umgebaut ist. Siehe dazu: [BSE-278](https://jira.service.zd.drv/browse/BSE-278) 

Füge die Dependencies im Zielprojekt (z. B. Quarkus-Microservice) ein:

```xml
<!-- Logging -->
<dependency>
   <groupId>de.drvbund.pruefdienst.logging</groupId>
   <artifactId>logging</artifactId>
   <version>0.1-SNAPSHOT</version>
</dependency>
<dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-logging-json</artifactId>
</dependency>
<dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-opentelemetry</artifactId>
</dependency>
<dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
<!-- Logging Ende -->
```

## Konfiguration des Loggings in Quarkus-Projekten
Damit in deinem Quarkus-Projekt auch richtig geloggt wird, muss die `application.properties` angepasst werden. Füge dafür folgende Zeilen hinzu:
```properties
# =========================================================
# Logging Konfiguration allgemein
# =========================================================

# Index dependency, damit JAX-RS Filter zum Setzen der Pflichtfelder im Log auch geladen werden (nach Umbau auf Quarkus-Extension nicht mehr notwendig)
quarkus.index-dependency.loglib.group-id=de.drvbund.pruefdienst.logging
quarkus.index-dependency.loglib.artifact-id=logging

# OTel
quarkus.otel.enabled=true
quarkus.otel.traces.enabled=true
quarkus.otel.metrics.enabled=true

# Prometheus
quarkus.micrometer.export.prometheus.enabled=true


# =========================================================
# Logging Konfiguration DEV
# =========================================================
%dev.quarkus.log.console.level=DEBUG
%dev.quarkus.log.console.json=false
%dev.quarkus.log.console.json.enabled=false
%dev.quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p message=%m service=%X{service.name} env=%X{environment} host=%h traceId=%X{traceId} spanId=%X{spanId} corrId=%X{correlationId} http.method=%X{http.method} http.path=%X{http.path}%n

# =========================================================
# Logging Konfiguration PROD (und andere Umgebungen != DEV)
# =========================================================
%prod.quarkus.log.console.level=INFO
%prod.quarkus.log.console.json=true
%prod.quarkus.log.console.json.enabled=true
%prod.quarkus.log.console.json.pretty-print=true
```

### Logging mit unterschiedlichen Profilen
In der `application.properties` finden sich zwei Profile:
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
 ./mvnw quarkus:dev -Dquarkus.profile=prod
```

## Verwendung des Loggers
**Wichtig:** Aktuell ist die LoggerFactory bzw. der LoggingWrapper nicht Depdency-Injection-fähig! Er muss über die Factory geladen werden. Diese Funktion gibt es erst, wenn die Lib als Quarkus-Extenstion umgebaut ist. Siehe dazu: [BSE-278](https://jira.service.zd.drv/browse/BSE-278)

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
