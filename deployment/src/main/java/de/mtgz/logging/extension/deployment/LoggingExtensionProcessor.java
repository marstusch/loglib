package de.mtgz.logging.extension.deployment;

import de.mtgz.logging.LoggingRequestFilter;
import de.mtgz.logging.LoggingResponseFilter;
import de.mtgz.logging.correlation.CorrelationIdClientRequestFilter;
import de.mtgz.logging.correlation.CorrelationIdRequestFilter;
import de.mtgz.logging.correlation.CorrelationIdResponseFilter;
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
                .addBeanClass(CorrelationIdClientRequestFilter.class)
                .addBeanClass(CorrelationIdRequestFilter.class)
                .addBeanClass(CorrelationIdResponseFilter.class)
                .setUnremovable()
                .build();
    }

    @BuildStep
    List<RunTimeConfigurationDefaultBuildItem> defaults() {
        return List.of(
                // Console JSON
                new RunTimeConfigurationDefaultBuildItem("quarkus.log.console.json", "true"),
                new RunTimeConfigurationDefaultBuildItem("quarkus.log.console.json.pretty-print", "true"),

                // OTel
                new RunTimeConfigurationDefaultBuildItem("quarkus.otel.enabled", "true"),
                new RunTimeConfigurationDefaultBuildItem("quarkus.otel.traces.enabled", "true"),
                new RunTimeConfigurationDefaultBuildItem("quarkus.otel.metrics.enabled", "true")
        );
    }
}
