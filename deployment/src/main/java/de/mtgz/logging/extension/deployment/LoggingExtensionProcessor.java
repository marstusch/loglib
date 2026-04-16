package de.mtgz.logging.extension.deployment;

import de.mtgz.logging.LoggingRequestFilter;
import de.mtgz.logging.LoggingResponseFilter;
import de.mtgz.logging.correlation.CorrelationIdClientRequestFilter;
import de.mtgz.logging.injection.LoggerProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

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
            .setUnremovable()
            .build();
   }
}
