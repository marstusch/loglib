package de.drvbund.pruefdienst.logging;

import de.drvbund.pruefdienst.logging.wrapper.LoggingWrapper;

public class LoggerFactory {

   private LoggerFactory() {
   }

   public static Logger getLogger(Class<?> clazz) {
      return new LoggingWrapper(clazz);
   }
}
