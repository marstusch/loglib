# Komponentendiagramm / Moduldiagramm

Dieses Diagramm zeigt die Maven-Module und wie die Quarkus-Extension-Mechanik Deployment- und Runtime-Anteile verbindet.

```plantuml
@startuml
skinparam componentStyle rectangle

package "loglib" {
  [runtime\n(logging-extension)] as Runtime
  [deployment\n(logging-extension-deployment)] as Deployment
  [integration-tests] as IT
}

component "LoggingExtensionProcessor\n(BuildStep)" as Processor
component "Quarkus Build" as Build
component "Runtime Beans\n(LoggerProducer, Filter, Mapper)" as Beans
component "ConfigSourceProvider\n(LoggingDefaultsConfigSourceProvider)" as Config

Deployment --> Runtime : dependency
IT --> Runtime : dependency
Build --> Processor : executes build steps
Processor --> Beans : AdditionalBeanBuildItem
Processor --> Build : FeatureBuildItem("logging-extension")
Runtime --> Config : service loader registration

@enduml
```

Hinweis: Die Defaults stammen aus einem `ConfigSourceProvider` im Runtime-Modul und können im Consumer über `application.properties` überschrieben werden.
