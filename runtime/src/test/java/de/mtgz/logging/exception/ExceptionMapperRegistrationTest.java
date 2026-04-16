package de.mtgz.logging.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.ext.Provider;

import org.junit.jupiter.api.Test;

class ExceptionMapperRegistrationTest {

   @Test
   void soll_keine_default_mapper_als_provider_registrieren() {
      assertThat(GenericExceptionMapper.class.isAnnotationPresent(Provider.class)).isFalse();
      assertThat(NotFoundExceptionMapper.class.isAnnotationPresent(Provider.class)).isFalse();
      assertThat(ValidationExceptionMapper.class.isAnnotationPresent(Provider.class)).isFalse();
      assertThat(WebApplicationExceptionMapper.class.isAnnotationPresent(Provider.class)).isFalse();
   }
}
