package de.mtgz.logging.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Member;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.mtgz.logging.Logger;
import de.mtgz.logging.wrapper.LoggingWrapper;
import jakarta.enterprise.inject.spi.InjectionPoint;

class LoggerProducerTest {

   @Test
   @Disabled
   void soll_logger_fuer_declaring_class_produzieren() {
      InjectionPoint injectionPoint = mock(InjectionPoint.class);
      Member member = mock(Member.class);
      when(injectionPoint.getMember()).thenReturn(member);
//      when(member.getDeclaringClass()).thenReturn(LoggerProducerTest.class);

      LoggerProducer loggerProducer = new LoggerProducer();
      Logger logger = loggerProducer.produce(injectionPoint);

      assertThat(logger).isInstanceOf(LoggingWrapper.class);
   }
}
