package de.drvbund.pruefdienst.logging.context;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingContextConfigTest {

   @Test
   void soll_serviceName_und_profile_auslesen() {
      Config config = ConfigProviderResolver.instance()
         .getBuilder()
         .withSources(new MapConfigSource(Map.of(
            LoggingContextConstants.SERVICE_NAME, "iphw-service",
            LoggingContextConstants.PROFILE_ENV, "prod")))
         .build();

      LoggingContextConfig contextConfig = new LoggingContextConfig(config);

      assertThat(contextConfig.getServiceName()).isEqualTo("iphw-service");
      assertThat(contextConfig.getEnvironment()).isEqualTo("prod");
   }

   @Test
   void soll_defaultnamen_liefern_wenn_serviceName_und_profile_leer() {
      Config config = ConfigProviderResolver.instance()
         .getBuilder()
         .withSources(new MapConfigSource(Map.of()))
         .build();

      LoggingContextConfig contextConfig = new LoggingContextConfig(config);

      assertThat(contextConfig.getServiceName()).isEqualTo("unbekannter Service-Name");
      assertThat(contextConfig.getEnvironment()).isEqualTo("unbekannte Umgebung");
   }
}