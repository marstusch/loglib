package de.mtgz.logging.injection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import de.mtgz.logging.Logger;
import de.mtgz.logging.LoggerFactory;

/**
 * Klasse für die Injection des {@link Logger} Interfaces
 */
@ApplicationScoped
public class LoggerProducer {

   @Produces
   @Dependent // wichtig: pro InjectionPoint auch immer eine eigene Instanz liefern
   public Logger produce(InjectionPoint ip) {
      return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
   }
}
