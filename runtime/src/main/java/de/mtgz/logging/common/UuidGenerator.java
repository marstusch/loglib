package de.mtgz.logging.common;

import java.util.UUID;

public class UuidGenerator {

   public String generate() {
      return UUID.randomUUID().toString();
   }
}
