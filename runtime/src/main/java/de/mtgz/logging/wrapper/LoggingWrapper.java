package de.mtgz.logging.wrapper;

import static de.mtgz.logging.wrapper.LogLevel.DEBUG;
import static de.mtgz.logging.wrapper.LogLevel.ERROR;
import static de.mtgz.logging.wrapper.LogLevel.INFO;
import static de.mtgz.logging.wrapper.LogLevel.TRACE;
import static de.mtgz.logging.wrapper.LogLevel.WARN;

import org.jboss.logging.Logger;

/**
 * Wrappt den JBoss-Logger und stellt alle benötigten Methoden bereit
 */
public class LoggingWrapper implements de.mtgz.logging.Logger {

   private final Logger logger;

   public LoggingWrapper() {
      this(Logger.getLogger(LoggingWrapper.class));
   }

   public LoggingWrapper(Class<?> clazz) {
      this(Logger.getLogger(clazz));
   }

   protected LoggingWrapper(Logger logger) {
      this.logger = logger;
   }

   @Override
   public void info(Object message) {
      log(INFO, message);
   }

   @Override
   public void info(Object message, Throwable t) {
      log(INFO, message, t);
   }

   @Override
   public void info(String className, Object message, Throwable t) {
      log(INFO, className, message, t);
   }

   @Override
   public void info(String className, Object message, Object[] params, Throwable t) {
      log(className, INFO, message, params, t);
   }

   @Override
   public void infov(String format, Object... params) {
      logv(INFO, format, params);
   }

   @Override
   public void infov(Throwable t, String format, Object... params) {
      logv(INFO, t, format, params);
   }

   @Override
   public void infov(String className, Throwable t, String format, Object... params) {
      logv(className, INFO, t, format, params);
   }

   @Override
   public void infof(String format, Object... params) {
      logf(INFO, format, params);
   }

   @Override
   public void infof(Throwable t, String format, Object... params) {
      logf(INFO, t, format, params);
   }

   @Override
   public void infof(String className, Throwable t, String format, Object... params) {
      logf(className, INFO, t, format, params);
   }

   @Override
   public void error(Object message) {
      log(ERROR, message);
   }

   @Override
   public void error(Object message, Throwable t) {
      log(ERROR, message, t);
   }

   @Override
   public void error(String className, Object message, Throwable t) {
      log(ERROR, className, message, t);
   }

   @Override
   public void error(String className, Object message, Object[] params, Throwable t) {
      log(className, ERROR, message, params, t);
   }

   @Override
   public void errorv(String format, Object... params) {
      logv(ERROR, format, params);
   }

   @Override
   public void errorv(Throwable t, String format, Object... params) {
      logv(ERROR, t, format, params);
   }

   @Override
   public void errorv(String className, Throwable t, String format, Object... params) {
      logv(className, ERROR, t, format, params);
   }

   @Override
   public void errorf(String format, Object... params) {
      logf(ERROR, format, params);
   }

   @Override
   public void errorf(Throwable t, String format, Object... params) {
      logf(ERROR, t, format, params);
   }

   @Override
   public void errorf(String className, Throwable t, String format, Object... params) {
      logf(className, ERROR, t, format, params);
   }

   @Override
   public void warn(Object message) {
      log(WARN, message);
   }

   @Override
   public void warn(Object message, Throwable t) {
      log(WARN, message, t);
   }

   @Override
   public void warn(String className, Object message, Throwable t) {
      log(WARN, className, message, t);
   }

   @Override
   public void warn(String className, Object message, Object[] params, Throwable t) {
      log(className, WARN, message, params, t);
   }

   @Override
   public void warnv(String format, Object... params) {
      logv(WARN, format, params);
   }

   @Override
   public void warnv(Throwable t, String format, Object... params) {
      logv(WARN, t, format, params);
   }

   @Override
   public void warnv(String className, Throwable t, String format, Object... params) {
      logv(className, WARN, t, format, params);
   }

   @Override
   public void warnf(String format, Object... params) {
      logf(WARN, format, params);
   }

   @Override
   public void warnf(Throwable t, String format, Object... params) {
      logf(WARN, t, format, params);
   }

   @Override
   public void warnf(String className, Throwable t, String format, Object... params) {
      logf(className, WARN, t, format, params);
   }

   @Override
   public void debug(Object message) {
      log(DEBUG, message);
   }

   @Override
   public void debug(Object message, Throwable t) {
      log(DEBUG, message, t);
   }

   @Override
   public void debug(String className, Object message, Throwable t) {
      log(DEBUG, className, message, t);
   }

   @Override
   public void debug(String className, Object message, Object[] params, Throwable t) {
      log(className, DEBUG, message, params, t);
   }

   @Override
   public void debugv(String format, Object... params) {
      logv(DEBUG, format, params);
   }

   @Override
   public void debugv(Throwable t, String format, Object... params) {
      logv(DEBUG, t, format, params);
   }

   @Override
   public void debugv(String className, Throwable t, String format, Object... params) {
      logv(className, DEBUG, t, format, params);
   }

   @Override
   public void debugf(String format, Object... params) {
      logf(DEBUG, format, params);
   }

   @Override
   public void debugf(Throwable t, String format, Object... params) {
      logf(DEBUG, t, format, params);
   }

   @Override
   public void debugf(String className, Throwable t, String format, Object... params) {
      logf(className, DEBUG, t, format, params);
   }

   @Override
   public void trace(Object message) {
      log(TRACE, message);
   }

   @Override
   public void trace(Object message, Throwable t) {
      log(TRACE, message, t);
   }

   @Override
   public void trace(String className, Object message, Throwable t) {
      log(TRACE, className, message, t);
   }

   @Override
   public void trace(String className, Object message, Object[] params, Throwable t) {
      log(className, TRACE, message, params, t);
   }

   @Override
   public void tracev(String format, Object... params) {
      logv(TRACE, format, params);
   }

   @Override
   public void tracev(Throwable t, String format, Object... params) {
      logv(TRACE, t, format, params);
   }

   @Override
   public void tracev(String className, Throwable t, String format, Object... params) {
      logv(className, TRACE, t, format, params);
   }

   @Override
   public void tracef(String format, Object... params) {
      logf(TRACE, format, params);
   }

   @Override
   public void tracef(Throwable t, String format, Object... params) {
      logf(TRACE, t, format, params);
   }

   @Override
   public void tracef(String className, Throwable t, String format, Object... params) {
      logf(className, TRACE, t, format, params);
   }

   @Override
   public void log(LogLevel level, Object message) {
      logger.log(level.mapToLoggerLevel(), message);
   }

   @Override
   public void log(LogLevel level, Object message, Throwable t) {
      logger.log(level.mapToLoggerLevel(), message, t);
   }

   @Override
   public void log(LogLevel level, String className, Object message, Throwable t) {
      logger.log(level.mapToLoggerLevel(), className, message, t);
   }

   @Override
   public void log(String className, LogLevel level, Object message, Object[] params, Throwable t) {
      logger.log(className, level.mapToLoggerLevel(), message, params, t);
   }

   @Override
   public void logv(LogLevel level, String format, Object... params) {
      logger.logv(level.mapToLoggerLevel(), format, params);
   }

   @Override
   public void logv(LogLevel level, Throwable t, String format, Object... params) {
      logger.logv(level.mapToLoggerLevel(), t, format, params);
   }

   @Override
   public void logv(String className, LogLevel level, Throwable t, String format, Object... params) {
      logger.logv(className, level.mapToLoggerLevel(), t, format, params);
   }

   @Override
   public void logf(LogLevel level, String format, Object... params) {
      logger.logf(level.mapToLoggerLevel(), format, params);
   }

   @Override
   public void logf(LogLevel level, Throwable t, String format, Object... params) {
      logger.logf(level.mapToLoggerLevel(), t, format, params);
   }

   @Override
   public void logf(String className, LogLevel level, Throwable t, String format, Object... params) {
      logger.logf(className, level.mapToLoggerLevel(), t, format, params);
   }
}
