package de.mtgz.logging.context;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Liefert konfigurierte Kontextinformationen für das Logging.
 */
public class LoggingContextConfig {

   private final Config config;

   /**
    * Erstellt eine Konfiguration basierend auf MicroProfile Config.
    */
   public LoggingContextConfig() {
      this(ConfigProvider.getConfig());
   }

   public LoggingContextConfig(Config config) {
      this.config = config;
   }

   /**
    * Bestimmt den Service-Namen.
    *
    * @return Service-Name oder Fallback
    */
   public String getServiceName() {
      return config.getOptionalValue(LoggingContextConstants.SERVICE_NAME, String.class)
         .filter(value -> !value.isBlank())
         .orElse("unknown-service");
   }

   /**
    * Bestimmt die Umgebung (Profile).
    *
    * @return Umgebungsname oder Fallback
    */
   public String getEnvironment() {
      return config.getOptionalValue(LoggingContextConstants.PROFILE_ENV, String.class)
         .filter(value -> !value.isBlank())
         .orElse("prod");
   }
}