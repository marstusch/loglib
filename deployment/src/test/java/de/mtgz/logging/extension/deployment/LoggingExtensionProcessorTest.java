package de.mtgz.logging.extension.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class LoggingExtensionProcessorTest {

   @Test
   void soll_feature_builditem_mit_erwartetem_namen_liefern() {
      LoggingExtensionProcessor processor = new LoggingExtensionProcessor();

      FeatureBuildItem featureBuildItem = processor.feature();

      assertThat(featureBuildItem.getInfo().getName()).isEqualTo("logging-extension");
   }

   @Test
   void soll_alle_erwarteten_beans_unremovable_registrieren() {
      LoggingExtensionProcessor processor = new LoggingExtensionProcessor();

      AdditionalBeanBuildItem beanBuildItem = processor.registerBeans();

      assertThat(beanBuildItem.isRemovable()).isFalse();
      assertThat(beanBuildItem.getBeanClasses())
         .contains(
            "de.mtgz.logging.injection.LoggerProducer",
            "de.mtgz.logging.LoggingRequestFilter",
            "de.mtgz.logging.LoggingResponseFilter",
            "de.mtgz.logging.correlation.CorrelationIdClientRequestFilter",
            "de.mtgz.logging.exception.GenericExceptionMapper",
            "de.mtgz.logging.exception.ValidationExceptionMapper",
            "de.mtgz.logging.exception.WebApplicationExceptionMapper");
   }
}
