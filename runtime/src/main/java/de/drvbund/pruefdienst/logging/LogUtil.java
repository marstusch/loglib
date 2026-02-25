package de.drvbund.pruefdienst.logging;

import de.drvbund.pruefdienst.logging.security.DefaultLogMasker;
import de.drvbund.pruefdienst.logging.security.LogMasker;
import de.drvbund.pruefdienst.logging.security.SHA256LogHasher;

/**
 * Utility-Klasse für erweiterte Funktionen, wie Masking und Hashing von Log-Values
 */
public class LogUtil {

   private static final LogMasker DEFAULT_LOG_MASKER = new DefaultLogMasker();
   private static final SHA256LogHasher SHA_256_LOG_HASHER = new SHA256LogHasher();

   private LogUtil() {
   }

   /**
    * Maskiert ein Objekt. Default-Suffix-Length ist 4.<br>
    * Beispiel:<br>
    * <code>
    * String iban = "DE12 3456 789 10 1112 1314 1516";<br>
    * Logger logger = LoggerFactory.getLogger(this.getClass());<br>
    * logger.infof("IBAN: %s", LogUtil.mask(iban.trim());<br>
    * </code><br>
    * Erzeugt in der Console:<br>
    * IBAN: *****************1516
    *
    * @param value Objekt, welches Maskiert werden soll
    * @return maskiertes Objekt
    */
   public static String mask(String value) {
      return DEFAULT_LOG_MASKER.mask(value);
   }

   /**
    * Hasht ein Objekt. Default-Salt ist eine stets neu erzeugte UUID.
    *
    * @param value Objekt, welches gehasht werden soll
    * @return gehashtes Objekt
    */
   public static String hash(String value) {
      return SHA_256_LOG_HASHER.hash(value);
   }
}
