package de.mtgz.logging.config;


import de.mtgz.logging.wrapper.LogLevel;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoggingDefaultsConfigSourceProvider implements ConfigSourceProvider {

   public static final String HUMAN_READABLE_FORMAT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p service=%X{service.name} env=%X{environment} host=%h traceId=%X{traceId} spanId=%X{spanId} corrId=%X{correlationId} http.method=%X{http.method} http.path=%X{http.path} message=%m %n";

   @Override
   public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
      return Collections.singletonList(new LoggingDefaultsConfigSource());
   }

   static class LoggingDefaultsConfigSource implements ConfigSource {

      private final Map<String, String> props;

      LoggingDefaultsConfigSource() {
         Map<String, String> p = new HashMap<>();

         // Allgemein
         p.put("quarkus.otel.enabled", "true");
         p.put("quarkus.otel.traces.enabled", "true");
         p.put("quarkus.otel.metrics.enabled", "true");

         // Dev
         p.put("%dev.quarkus.log.level", LogLevel.DEBUG.name());
         p.put("%dev.quarkus.log.console.level", LogLevel.DEBUG.name());
         p.put("%dev.quarkus.log.console.json.enabled", "false");
         p.put("%dev.quarkus.log.console.format", HUMAN_READABLE_FORMAT);

         // Test
         p.put("%test.quarkus.log.level", LogLevel.DEBUG.name());
         p.put("%test.quarkus.log.console.level", LogLevel.DEBUG.name());
         p.put("%test.quarkus.log.console.json.enabled", "false");
         p.put("%test.quarkus.log.console.format", HUMAN_READABLE_FORMAT);

         // Prod
         p.put("%prod.quarkus.log.level", LogLevel.INFO.name());
         p.put("%prod.quarkus.log.console.level", LogLevel.INFO.name());
         p.put("%prod.quarkus.log.console.json.enabled", "true");
         p.put("%prod.quarkus.log.console.json.pretty-print", "true");

         this.props = Collections.unmodifiableMap(p);
      }

      @Override
      public Map<String, String> getProperties() {
         return props;
      }

      @Override
      public Set<String> getPropertyNames() {
         return props.keySet();
      }

      @Override
      public String getValue(String propertyName) {
         return props.get(propertyName);
      }

      @Override
      public String getName() {
         return "logging-defaults";
      }

      @Override
      public int getOrdinal() {
         // Wichtig: höher als "Default Values", aber unterhalb von application.properties (250)
         // damit Services jederzeit überschreiben können (microprofile-config.properties = 100)
         return 90;
      }
   }
}