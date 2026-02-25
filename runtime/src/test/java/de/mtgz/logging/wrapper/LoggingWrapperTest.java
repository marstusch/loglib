package de.mtgz.logging.wrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LoggingWrapperTest {

   private static final String TEST_FULLY_QUALIFIED_CLASS_NAME = "FQCN";
   private static final Throwable TEST_THROWABLE = new RuntimeException("TestThrowable");

   private static LoggingWrapper loggingWrapper;
   private static Logger logger;

   @BeforeAll
   static void initializeLoggers() {
      logger = mock(Logger.class);
      loggingWrapper = new LoggingWrapper(logger);
   }

   @Test
   void soll_log_level_message_methode_aus_logger_verwenden() {
      loggingWrapper.log(LogLevel.DEBUG, "Test");

      verify(logger).log(Logger.Level.DEBUG, "Test");
   }

   @Test
   void soll_log_level_message_throwable_methode_aus_logger_verwenden() {
      loggingWrapper.log(LogLevel.DEBUG,
         "Test",
         TEST_THROWABLE);

      verify(logger).log(Logger.Level.DEBUG,
         "Test",
         TEST_THROWABLE);
   }

   @Test
   void soll_log_level_classname_message_throwable_methode_aus_logger_verwenden() {
      loggingWrapper.log(LogLevel.DEBUG,
         TEST_FULLY_QUALIFIED_CLASS_NAME,
         "Test",
         TEST_THROWABLE);

      verify(logger).log(Logger.Level.DEBUG,
         TEST_FULLY_QUALIFIED_CLASS_NAME,
         "Test",
         TEST_THROWABLE);
   }

   @Test
   void soll_log_classname_level_message_params_throwable_methode_aus_logger_verwenden() {
      loggingWrapper.log(TEST_FULLY_QUALIFIED_CLASS_NAME,
         LogLevel.DEBUG,
         "Test",
         new Object[] {1, 2},
         TEST_THROWABLE);

      verify(logger).log(TEST_FULLY_QUALIFIED_CLASS_NAME,
         Logger.Level.DEBUG,
         "Test",
         new Object[] {1, 2},
         TEST_THROWABLE);
   }

   @Test
   void soll_logv_level_format_params_methode_aus_logger_verwenden() {
      loggingWrapper.logv(LogLevel.DEBUG,
         "Test",
         1, 2, 3);

      verify(logger).logv(Logger.Level.DEBUG,
         "Test",
         new Object[] {1, 2, 3});
   }

   @Test
   void soll_logv_level_throwable_format_params_methode_aus_logger_verwenden() {
      loggingWrapper.logv(LogLevel.DEBUG,
         TEST_THROWABLE,
         "Test",
         1, 2, 3);

      verify(logger).logv(Logger.Level.DEBUG,
         TEST_THROWABLE,
         "Test",
         new Object[] {1, 2, 3});
   }

   @Test
   void soll_logv_classname_level_throwable_format_params_methode_aus_logger_verwenden() {
      loggingWrapper.logv(TEST_FULLY_QUALIFIED_CLASS_NAME,
         LogLevel.DEBUG,
         TEST_THROWABLE,
         "Test",
         1, 2, 3);

      verify(logger).logv(TEST_FULLY_QUALIFIED_CLASS_NAME,
         Logger.Level.DEBUG,
         TEST_THROWABLE,
         "Test",
         new Object[] {1, 2, 3});
   }

   @Test
   void soll_logf_level_format_params_methode_aus_logger_verwenden() {
      loggingWrapper.logf(LogLevel.DEBUG,
         "Test",
         1, 2, 3);

      verify(logger).logf(Logger.Level.DEBUG,
         "Test",
         new Object[] {1, 2, 3});
   }

   @Test
   void soll_logf_level_throwable_format_params_methode_aus_logger_verwenden() {
      loggingWrapper.logf(LogLevel.DEBUG,
         TEST_THROWABLE,
         "Test",
         1, 2, 3);

      verify(logger).logf(Logger.Level.DEBUG,
         TEST_THROWABLE,
         "Test",
         new Object[] {1, 2, 3});
   }

   @Test
   void soll_logf_classname_level_throwable_format_params_methode_aus_logger_verwenden() {
      loggingWrapper.logf(TEST_FULLY_QUALIFIED_CLASS_NAME,
         LogLevel.DEBUG,
         TEST_THROWABLE,
         "Test",
         1, 2, 3);

      verify(logger).logf(TEST_FULLY_QUALIFIED_CLASS_NAME,
         Logger.Level.DEBUG,
         TEST_THROWABLE,
         "Test",
         new Object[] {1, 2, 3});
   }

   @Test
   void soll_loggingwrapper_level_zu_logger_level_mappen() {
      Logger.Level traceLevel = LogLevel.TRACE.mapToLoggerLevel();
      Logger.Level debugLevel = LogLevel.DEBUG.mapToLoggerLevel();
      Logger.Level infoLevel = LogLevel.INFO.mapToLoggerLevel();
      Logger.Level warnLevel = LogLevel.WARN.mapToLoggerLevel();
      Logger.Level errorLevel = LogLevel.ERROR.mapToLoggerLevel();

      assertEquals(Logger.Level.TRACE, traceLevel);
      assertEquals(Logger.Level.DEBUG, debugLevel);
      assertEquals(Logger.Level.INFO, infoLevel);
      assertEquals(Logger.Level.WARN, warnLevel);
      assertEquals(Logger.Level.ERROR, errorLevel);
   }
}