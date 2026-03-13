package de.mtgz.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LogUtilTest {

   @Test
   void soll_null_bei_mask_null_liefern() {
      String maskedValue = LogUtil.mask(null);

      assertThat(maskedValue).isNull();
   }

   @Test
   void soll_value_maskieren() {
      String maskedValue = LogUtil.mask("abcdefghij");

      assertThat(maskedValue).isEqualTo("******ghij");
   }

   @Test
   void soll_hash_bei_selbem_input_stabil_liefern() {
      String firstHash = LogUtil.hash("wert-1");
      String secondHash = LogUtil.hash("wert-1");

      assertThat(firstHash).isEqualTo(secondHash);
      assertThat(firstHash).isNotEqualTo("wert-1");
   }
}
