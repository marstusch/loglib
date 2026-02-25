package de.mtgz.logging.extension.deployment;

import de.mtgz.logging.LoggingRequestFilter;
import de.mtgz.logging.LoggingResponseFilter;
import de.mtgz.logging.correlation.CorrelationIdClientRequestFilter;
import de.mtgz.logging.correlation.CorrelationIdRequestFilter;
import de.mtgz.logging.correlation.CorrelationIdResponseFilter;
import de.mtgz.logging.exception.GenericExceptionMapper;
import de.mtgz.logging.exception.ValidationExceptionMapper;
import de.mtgz.logging.exception.WebApplicationExceptionMapper;
import de.mtgz.logging.injection.LoggerProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;

import java.util.List;

class LoggingExtensionProcessor {

    private static final String FEATURE = "logging-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem registerBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClass(LoggerProducer.class)
                .addBeanClass(LoggingRequestFilter.class)
                .addBeanClass(LoggingResponseFilter.class)
                .addBeanClass(GenericExceptionMapper.class)
                .addBeanClass(ValidationExceptionMapper.class)
                .addBeanClass(WebApplicationExceptionMapper.class)
                .addBeanClass(CorrelationIdClientRequestFilter.class)
                .setUnremovable()
                .build();
    }

    @BuildStep
    List<RunTimeConfigurationDefaultBuildItem> defaults() {
        return List.of(
                // =========================================================
                // Logging Konfiguration allgemein
                // =========================================================

                // OTel
                new RunTimeConfigurationDefaultBuildItem("quarkus.otel.enabled", "true"),
                new RunTimeConfigurationDefaultBuildItem("quarkus.otel.traces.enabled", "true"),
                new RunTimeConfigurationDefaultBuildItem("quarkus.otel.metrics.enabled", "true"),

                // Prometheus
                new RunTimeConfigurationDefaultBuildItem("quarkus.micrometer.export.prometheus.enabled", "true"),

                // =========================================================
                // Logging Konfiguration DEV
                // =========================================================
                new RunTimeConfigurationDefaultBuildItem("%dev.quarkus.log.console.level", "DEBUG"),
                new RunTimeConfigurationDefaultBuildItem("%dev.quarkus.log.console.json", "false"),
                new RunTimeConfigurationDefaultBuildItem(
                        "%dev.quarkus.log.console.format",
                        "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p message=%m service=%X{service.name} env=%X{environment} host=%h traceId=%X{traceId} spanId=%X{spanId} corrId=%X{correlationId} http.method=%X{http.method} http.path=%X{http.path}%n"
                ),

                // =========================================================
                // Logging Konfiguration PROD (und andere Umgebungen != DEV)
                // =========================================================
                new RunTimeConfigurationDefaultBuildItem("%prod.quarkus.log.console.level", "INFO"),
                new RunTimeConfigurationDefaultBuildItem("%prod.quarkus.log.console.json", "true"),
                new RunTimeConfigurationDefaultBuildItem("%prod.quarkus.log.console.json.pretty-print", "true")
        );
    }
}
