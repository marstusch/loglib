package de.drvbund.pruefdienst.logging.wrapper;

import org.jboss.logging.Logger;

/**
 * Wrappt das JBoss-Logger.Level
 */
public enum LogLevel {

   // Normale Ereignisse (Standard-Level)
   INFO,

   // Fehler, die Aktionen erforderlich machen
   ERROR,

   // unerwartete, aber tolerierte Zustände
   WARN,

   // Ausgaben während der Entwicklung bzw. Testung
   DEBUG,

   // nur lokal zu verwenden
   TRACE;

   /**
    * Wandelt das LoggingWrapper.Level in JBoss-Logger.Level um
    *
    * @return Logger.Level
    */
   public Logger.Level mapToLoggerLevel() {
      return switch (this) {
         case INFO -> Logger.Level.INFO;
         case ERROR -> Logger.Level.ERROR;
         case WARN -> Logger.Level.WARN;
         case DEBUG -> Logger.Level.DEBUG;
         case TRACE -> Logger.Level.TRACE;
      };
   }
}
