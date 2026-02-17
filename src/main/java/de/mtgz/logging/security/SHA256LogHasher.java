package de.mtgz.logging.security;

import de.mtgz.logging.common.UuidGenerator;
import org.apache.commons.codec.digest.DigestUtils;

public final class SHA256LogHasher implements LogHasher {

   private final String salt;

   public SHA256LogHasher() {
      this(new UuidGenerator().generate());
   }

   SHA256LogHasher(String salt) {
      this.salt = salt;
   }

   @Override
   public String hash(String value) {
      if (value == null) {
         return null;
      }
      return DigestUtils.sha256Hex(salt + value);
   }
}
