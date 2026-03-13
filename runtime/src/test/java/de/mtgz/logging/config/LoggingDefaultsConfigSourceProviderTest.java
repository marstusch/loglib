package de.mtgz.logging.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.Test;

class LoggingDefaultsConfigSourceProviderTest {

   @Test
   void soll_default_configsource_mit_ordinal_und_name_liefern() {
      LoggingDefaultsConfigSourceProvider provider = new LoggingDefaultsConfigSourceProvider();

      ConfigSource configSource = provider.getConfigSources(getClass().getClassLoader()).iterator().next();

      assertThat(configSource.getName()).isEqualTo("logging-defaults");
      assertThat(configSource.getOrdinal()).isEqualTo(90);
   }

   @Test
   void soll_dev_test_und_prod_defaultwerte_liefern() {
      LoggingDefaultsConfigSourceProvider provider = new LoggingDefaultsConfigSourceProvider();
      ConfigSource configSource = provider.getConfigSources(getClass().getClassLoader()).iterator().next();

      assertThat(configSource.getValue("quarkus.otel.enabled")).isEqualTo("true");
      assertThat(configSource.getValue("%dev.quarkus.log.level")).isEqualTo("DEBUG");
      assertThat(configSource.getValue("%test.quarkus.log.console.format"))
         .isEqualTo(LoggingDefaultsConfigSourceProvider.HUMAN_READABLE_FORMAT);
      assertThat(configSource.getValue("%prod.quarkus.log.console.json.enabled")).isEqualTo("true");
      assertThat(configSource.getPropertyNames()).contains("%prod.quarkus.log.console.json.pretty-print");
      assertThat(configSource.getProperties()).containsEntry("%prod.quarkus.log.level", "INFO");
   }
}
