package de.mtgz.logging;

import de.mtgz.logging.wrapper.LoggingWrapper;

public class LoggerFactory {

   private LoggerFactory() {
   }

   public static Logger getLogger(Class<?> clazz) {
      return new LoggingWrapper(clazz);
   }
}
