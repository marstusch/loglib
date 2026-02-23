package de.mtgz.logging.deployment;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class MtgzLoggingProcessorTest {

   @RegisterExtension
   static final QuarkusUnitTest UNIT_TEST = new QuarkusUnitTest()
      .withApplicationRoot((jar) -> jar.addClasses(LoggingExtensionTestResource.class));

   @Test
   void shouldApplyDefaultsAndPopulateMdc() {
      Map<String, String> response = given()
         .when()
         .get("/log-test")
         .then()
         .statusCode(200)
         .extract()
         .as(Map.class);

      assertThat(response.get("jsonEnabled")).isEqualTo("true");
      assertThat(response.get("serviceField")).isEqualTo("${quarkus.application.name:unknown-service}");
      assertThat(response.get("environmentField")).isEqualTo("${quarkus.profile:prod}");
      assertThat(response.get("correlationId")).isNotBlank();
      assertThat(response.get("httpMethod")).isEqualTo("GET");
      assertThat(response.get("httpPath")).isEqualTo("log-test");
      assertThat(response.get("traceId")).isNotBlank();
      assertThat(response.get("spanId")).isNotBlank();
   }
}
