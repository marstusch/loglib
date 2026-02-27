# logging-extension

## Tests ausführen

- Alle Module: `mvn test`
- Nur Runtime (inkl. Unit- und Integrations-Tests): `mvn -pl runtime test`

Die Integrations-Tests verwenden `@QuarkusTest` mit einer minimalen Test-Ressource unter `/it/*` und validieren u.a. Correlation-Header, MDC-Snapshot, Outbound-Propagation sowie Error-Mapping.
