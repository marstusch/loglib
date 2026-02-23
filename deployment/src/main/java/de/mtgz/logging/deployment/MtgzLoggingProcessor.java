package de.mtgz.logging.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;
import io.quarkus.deployment.builditem.nativeimage.IndexDependencyBuildItem;

import de.mtgz.logging.LoggingRequestFilter;
import de.mtgz.logging.LoggingResponseFilter;
import de.mtgz.logging.correlation.CorrelationIdClientRequestFilter;
import de.mtgz.logging.correlation.CorrelationIdRequestFilter;
import de.mtgz.logging.correlation.CorrelationIdResponseFilter;
import de.mtgz.logging.exception.GenericExceptionMapper;
import de.mtgz.logging.exception.NotFoundExceptionMapper;
import de.mtgz.logging.exception.ValidationExceptionMapper;

class MtgzLoggingProcessor {

   @BuildStep
   IndexDependencyBuildItem indexRuntimeDependency() {
      return new IndexDependencyBuildItem("de.mtgz.logging", "quarkus-mtgz-logging");
   }

   @BuildStep
   AdditionalBeanBuildItem registerProvidersAndFilters() {
      return AdditionalBeanBuildItem.builder()
         .setUnremovable()
         .addBeanClasses(
            LoggingRequestFilter.class,
            LoggingResponseFilter.class,
            CorrelationIdRequestFilter.class,
            CorrelationIdResponseFilter.class,
            CorrelationIdClientRequestFilter.class,
            GenericExceptionMapper.class,
            NotFoundExceptionMapper.class,
            ValidationExceptionMapper.class)
         .build();
   }

   @BuildStep
   void runtimeDefaults(io.quarkus.deployment.annotations.BuildProducer<RunTimeConfigurationDefaultBuildItem> defaults) {
      defaults.produce(new RunTimeConfigurationDefaultBuildItem("quarkus.log.console.json", "true"));
      defaults.produce(new RunTimeConfigurationDefaultBuildItem(
         "quarkus.log.console.json.additional-field.service.value",
         "${quarkus.application.name:unknown-service}"));
      defaults.produce(new RunTimeConfigurationDefaultBuildItem(
         "quarkus.log.console.json.additional-field.environment.value",
         "${quarkus.profile:prod}"));
      defaults.produce(new RunTimeConfigurationDefaultBuildItem("quarkus.otel.enabled", "true"));
      defaults.produce(new RunTimeConfigurationDefaultBuildItem("quarkus.otel.traces.enabled", "true"));
   }
}
