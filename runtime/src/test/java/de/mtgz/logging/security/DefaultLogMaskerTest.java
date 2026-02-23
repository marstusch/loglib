package de.mtgz.logging.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DefaultLogMaskerTest {

   private static final int TEST_SUFFIXLENGTH = 4;
   private final DefaultLogMasker logValueMasker = new DefaultLogMasker(TEST_SUFFIXLENGTH);

   @Test
   void soll_null_ausgeben_wenn_value_null_ist() {

      String value = null;

      String valueActual = logValueMasker.mask(value);

      assertEquals(value, valueActual);
   }

   @Test
   void soll_leeren_String_ausgeben_wenn_value_leer_ist() {

      String value = "";

      String valueActual = logValueMasker.mask(value);

      assertEquals(value, valueActual);
   }

   @Test
   void soll_kurzen_value_komplett_maskieren() {

      String value = "test";

      String valueActual = logValueMasker.mask(value);

      assertEquals("****", valueActual);
   }

   @Test
   void soll_laengeren_value_teilweise_maskieren() {

      String value = "test-string";

      String valueActual = logValueMasker.mask(value);

      assertEquals("*******ring", valueActual);
   }

   @Test
   void soll_laenge_beim_maskieren_beibehalten() {

      String value = "abcdefghij";

      String valueActual = logValueMasker.mask(value);

      assertEquals(value.length(), valueActual.length());
   }

   @Test
   void soll_korrekte_anzahl_maskieren() {

      String value = "abcdefghij";

      String valueActual = logValueMasker.mask(value);

      int lengthActualWithoutMask = valueActual.replace("*", "").length();
      assertEquals(TEST_SUFFIXLENGTH, lengthActualWithoutMask);
   }

}