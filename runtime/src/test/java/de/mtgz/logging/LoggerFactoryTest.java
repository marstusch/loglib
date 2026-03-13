package de.mtgz.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.mtgz.logging.wrapper.LoggingWrapper;

class LoggerFactoryTest {

   @Test
   void soll_loggingwrapper_fuer_klasse_liefern() {
      Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);

      assertThat(logger).isInstanceOf(LoggingWrapper.class);
   }
}
