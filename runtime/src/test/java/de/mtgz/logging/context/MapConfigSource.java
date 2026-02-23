package de.mtgz.logging.context;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;
import java.util.Set;

public class MapConfigSource implements ConfigSource {

   private final Map<String, String> values;

   public MapConfigSource(Map<String, String> values) {
      this.values = values;
   }

   @Override
   public Map<String, String> getProperties() {
      return values;
   }

   @Override
   public Set<String> getPropertyNames() {
      return Set.of();
   }

   @Override
   public int getOrdinal() {
      return ConfigSource.super.getOrdinal();
   }

   @Override
   public String getValue(String propertyName) {
      return values.get(propertyName);
   }

   @Override
   public String getName() {
      return "map-config";
   }
}