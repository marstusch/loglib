package de.mtgz.logging.security;

public class DefaultLogMasker implements LogMasker {

   private final int suffixLength;

   public DefaultLogMasker() {
      this(4);
   }

   DefaultLogMasker(int suffixLength) {
      this.suffixLength = suffixLength;
   }

   @Override
   public String mask(String value) {
      if (value == null || value.isEmpty()) {
         return value;
      }

      int length = value.length();

      if (length <= suffixLength) {
         return "*".repeat(length);
      }
      int maskedLength = length - suffixLength;
      return "*".repeat(maskedLength) + value.substring(maskedLength);
   }
}
