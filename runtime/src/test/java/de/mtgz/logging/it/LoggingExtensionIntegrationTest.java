package de.mtgz.logging.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.correlation.CorrelationIdConstants;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

@QuarkusTest
class LoggingExtensionIntegrationTest {

   @Test
   void scenarioA_happyPath_setsMdcAndCorrelationIdAndReusesProvidedHeader() {
      Response withoutHeader = given()
         .when().get("/it/ok")
         .then()
         .statusCode(200)
         .extract().response();

      String generatedCorrelationId = withoutHeader.header(CorrelationIdConstants.HEADER_NAME);
      assertThat(generatedCorrelationId).isNotBlank();
      assertThat(withoutHeader.jsonPath().getString(CorrelationIdConstants.MDC_KEY)).isEqualTo(generatedCorrelationId);
      assertThat(withoutHeader.jsonPath().getString(LoggingConstants.HTTP_METHOD_KEY)).isEqualTo("GET");
      assertThat(withoutHeader.jsonPath().getString(LoggingConstants.HTTP_PATH_KEY)).isEqualTo("it/ok");
      assertThat(withoutHeader.jsonPath().getString(LoggingConstants.SERVICE_NAME_KEY)).isEqualTo("loglib-it");
      assertThat(withoutHeader.jsonPath().getString(LoggingConstants.ENVIRONMENT_KEY)).isEqualTo("test");

      String providedCorrelationId = "provided-correlation-id";
      Response withHeader = given()
         .header(CorrelationIdConstants.HEADER_NAME, providedCorrelationId)
         .when().get("/it/ok")
         .then()
         .statusCode(200)
         .extract().response();

      assertThat(withHeader.header(CorrelationIdConstants.HEADER_NAME)).isEqualTo(providedCorrelationId);
      assertThat(withHeader.jsonPath().getString(CorrelationIdConstants.MDC_KEY)).isEqualTo(providedCorrelationId);
   }

   @Test
   void scenarioB_outboundPropagation_usesSameCorrelationIdForDownstream() {
      Response response = given()
         .when().get("/it/call")
         .then()
         .statusCode(200)
         .extract().response();

      String responseCorrelationId = response.header(CorrelationIdConstants.HEADER_NAME);
      String requestCorrelationId = response.jsonPath().getString("requestCorrelationId");
      String downstreamCorrelationId = response.jsonPath().getString("downstreamCorrelationId");

      assertThat(responseCorrelationId).isNotBlank();
      assertThat(requestCorrelationId).isEqualTo(responseCorrelationId);
      assertThat(downstreamCorrelationId).isEqualTo(responseCorrelationId);
   }

   @Test
   void scenarioC_exceptionHandling_addsErrorIdHeaderAndConsistentBody() {
      Response boom = given()
         .when().get("/it/boom")
         .then()
         .statusCode(500)
         .extract().response();

      String errorId500 = boom.header(LoggingConstants.ERROR_ID_HEADER);
      assertThat(errorId500).isNotBlank();
      assertThat(boom.jsonPath().getString("errorId")).isEqualTo(errorId500);
      assertThat(boom.jsonPath().getInt("status")).isEqualTo(500);

      Response validation = given()
         .when().get("/it/validation")
         .then()
         .statusCode(400)
         .extract().response();

      String errorId400 = validation.header(LoggingConstants.ERROR_ID_HEADER);
      assertThat(errorId400).isNotBlank();
      assertThat(validation.jsonPath().getString("errorId")).isEqualTo(errorId400);
      assertThat(validation.jsonPath().getInt("status")).isEqualTo(400);
   }

   @Test
   void scenarioD_mdcCleanup_noCorrelationLeakAcrossRequests() {
      Response first = given()
         .when().get("/it/ok")
         .then().statusCode(200)
         .extract().response();

      Response second = given()
         .when().get("/it/ok")
         .then().statusCode(200)
         .extract().response();

      String firstCorrelationId = first.header(CorrelationIdConstants.HEADER_NAME);
      String secondCorrelationId = second.header(CorrelationIdConstants.HEADER_NAME);

      assertThat(firstCorrelationId).isNotBlank();
      assertThat(secondCorrelationId).isNotBlank();
      assertThat(secondCorrelationId).isNotEqualTo(firstCorrelationId);
      assertThat(second.jsonPath().getString(CorrelationIdConstants.MDC_KEY)).isEqualTo(secondCorrelationId);
   }

   @Test
   void scenarioE_fanout_propagatesOneCorrelationIdToAllDownstreamsAndUsesNewOneForSecondRequest() {
      Response first = given()
         .when().get("/it/fanout")
         .then().statusCode(200)
         .extract().response();

      String corr1 = first.header(CorrelationIdConstants.HEADER_NAME);
      assertThat(corr1).isNotBlank();
      assertThat(first.jsonPath().getString("requestCorrelationId")).isEqualTo(corr1);
      assertThat(first.jsonPath().getString("downstreamA.correlationId")).isEqualTo(corr1);
      assertThat(first.jsonPath().getString("downstreamB.correlationId")).isEqualTo(corr1);

      Response second = given()
         .when().get("/it/fanout")
         .then().statusCode(200)
         .extract().response();

      String corr2 = second.header(CorrelationIdConstants.HEADER_NAME);
      assertThat(corr2).isNotBlank();
      assertThat(corr2).isNotEqualTo(corr1);
      assertThat(second.jsonPath().getString("downstreamA.correlationId")).isEqualTo(corr2);
      assertThat(second.jsonPath().getString("downstreamB.correlationId")).isEqualTo(corr2);

      String provided = "corr-explicit";
      Response explicit = given()
         .header(CorrelationIdConstants.HEADER_NAME, provided)
         .when().get("/it/fanout")
         .then().statusCode(200)
         .extract().response();

      assertThat(explicit.header(CorrelationIdConstants.HEADER_NAME)).isEqualTo(provided);
      assertThat(explicit.jsonPath().getString("downstreamA.correlationId")).isEqualTo(provided);
      assertThat(explicit.jsonPath().getString("downstreamB.correlationId")).isEqualTo(provided);
   }
}
