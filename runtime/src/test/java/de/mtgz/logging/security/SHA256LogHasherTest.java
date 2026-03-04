package de.mtgz.logging.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class SHA256LogHasherTest {

   private static final String TEST_SALT = "test-salt";
   private final SHA256LogHasher logValueHasher = new SHA256LogHasher(TEST_SALT);

   @Test
   void soll_selben_hash_ausgeben_bei_selben_value() {

      String value = "test";

      String hashedStringExpected = logValueHasher.hash(value);
      String hashedStringActual = logValueHasher.hash(value);

      assertEquals(hashedStringExpected, hashedStringActual);
   }

   @Test
   void soll_anderen_hash_ausgeben_bei_unterschiedlichen_values() {

      String value = "test";
      String differentValue = "anderer test";

      String hashedString = logValueHasher.hash(value);
      String hashedStringDifferentValue = logValueHasher.hash(differentValue);

      assertNotEquals(hashedString, hashedStringDifferentValue);
   }

   @Test
   void soll_null_ausgeben_bei_null_value() {

      String value = null;

      String hashedStringActual = logValueHasher.hash(value);

      assertEquals(value, hashedStringActual);
   }

   @Test
   void soll_nicht_value_wieder_ausgeben() {

      String value = "test";

      String hashedStringActual = logValueHasher.hash(value);

      assertNotEquals(value, hashedStringActual);
   }

   @Test
   void soll_vom_salt_abhaengig_sein() {

      String value = "test";
      SHA256LogHasher hasher = new SHA256LogHasher("anderer-salt");

      String hashedStringActual = logValueHasher.hash(value);
      String hashedValue = hasher.hash(value);

      assertNotEquals(hashedValue, hashedStringActual);
   }
}