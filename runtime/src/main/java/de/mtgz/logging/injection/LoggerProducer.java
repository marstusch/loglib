package de.mtgz.logging.injection;

import de.mtgz.logging.Logger;
import de.mtgz.logging.wrapper.LoggingWrapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

/**
 * Klasse für die Injection des {@link Logger} Interfaces
 */
@ApplicationScoped
public class LoggerProducer {

   @Produces
   @Dependent // wichtig: pro InjectionPoint auch immer eine eigene Instanz liefern
   Logger produce(InjectionPoint ip) {
      return new LoggingWrapper(ip.getMember().getDeclaringClass());
   }
}
