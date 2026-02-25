package de.drvbund.pruefdienst.logging;

import de.drvbund.pruefdienst.logging.wrapper.LogLevel;

public interface Logger {

   void info(Object message);
   
   void info(Object message, Throwable t);

   void info(String className, Object message, Throwable t);

   void info(String className, Object message, Object[] params, Throwable t);

   void infov(String format, Object... params);

   void infov(Throwable t, String format, Object... params);

   void infov(String className, Throwable t, String format, Object... params);

   void infof(String format, Object... params);

   void infof(Throwable t, String format, Object... params);

   void infof(String className, Throwable t, String format, Object... params);

   void error(Object message);
   
   void error(Object message, Throwable t);

   void error(String className, Object message, Throwable t);

   void error(String className, Object message, Object[] params, Throwable t);

   void errorv(String format, Object... params);

   void errorv(Throwable t, String format, Object... params);

   void errorv(String className, Throwable t, String format, Object... params);

   void errorf(String format, Object... params);

   void errorf(Throwable t, String format, Object... params);

   void errorf(String className, Throwable t, String format, Object... params);

   void warn(Object message);
   
   void warn(Object message, Throwable t);

   void warn(String className, Object message, Throwable t);

   void warn(String className, Object message, Object[] params, Throwable t);

   void warnv(String format, Object... params);

   void warnv(Throwable t, String format, Object... params);

   void warnv(String className, Throwable t, String format, Object... params);

   void warnf(String format, Object... params);

   void warnf(Throwable t, String format, Object... params);

   void warnf(String className, Throwable t, String format, Object... params);

   void debug(Object message);
   
   void debug(Object message, Throwable t);

   void debug(String className, Object message, Throwable t);

   void debug(String className, Object message, Object[] params, Throwable t);

   void debugv(String format, Object... params);

   void debugv(Throwable t, String format, Object... params);

   void debugv(String className, Throwable t, String format, Object... params);

   void debugf(String format, Object... params);

   void debugf(Throwable t, String format, Object... params);

   void debugf(String className, Throwable t, String format, Object... params);

   void trace(Object message);
   
   void trace(Object message, Throwable t);

   void trace(String className, Object message, Throwable t);

   void trace(String className, Object message, Object[] params, Throwable t);

   void tracev(String format, Object... params);

   void tracev(Throwable t, String format, Object... params);

   void tracev(String className, Throwable t, String format, Object... params);

   void tracef(String format, Object... params);

   void tracef(Throwable t, String format, Object... params);

   void tracef(String className, Throwable t, String format, Object... params);

   void log(LogLevel level, Object message);

   void log(LogLevel level, Object message, Throwable t);

   void log(LogLevel level, String className, Object message, Throwable t);

   void log(String className, LogLevel level, Object message, Object[] params, Throwable t);

   void logv(LogLevel level, String format, Object... params);

   void logv(LogLevel level, Throwable t, String format, Object... params);

   void logv(String className, LogLevel level, Throwable t, String format, Object... params);

   void logf(LogLevel level, String format, Object... params);

   void logf(LogLevel level, Throwable t, String format, Object... params);

   void logf(String className, LogLevel level, Throwable t, String format, Object... params);
}
