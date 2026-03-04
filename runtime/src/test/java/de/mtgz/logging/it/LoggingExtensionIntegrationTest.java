package de.mtgz.logging.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import de.mtgz.logging.common.LoggingConstants;
import de.mtgz.logging.correlation.CorrelationIdConstants;
import de.mtgz.logging.exception.ErrorResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@QuarkusTest
class LoggingExtensionIntegrationTest {

   @Test
   void scenarioA_generatesAndReusesCorrelationIdAndSetsMdcFields() {
      Response generatedResponse = given()
         .when()
         .get("/it/ok")
         .then()
         .statusCode(200)
         .extract()
         .response();

      String generatedCorrelationId = generatedResponse.getHeader(CorrelationIdConstants.HEADER_NAME);
      assertThat(generatedCorrelationId).isNotBlank();

      Map<String, String> generatedBody = generatedResponse.as(new TypeRef<>() {
      });
      assertThat(generatedBody)
         .containsEntry(CorrelationIdConstants.MDC_KEY, generatedCorrelationId)
         .containsEntry(LoggingConstants.HTTP_METHOD_KEY, "GET")
         .containsEntry(LoggingConstants.HTTP_PATH_KEY, "/it/ok")
         .containsEntry(LoggingConstants.SERVICE_NAME_KEY, "loglib-it-service")
         .containsEntry(LoggingConstants.ENVIRONMENT_KEY, "itest");

      String incomingCorrelationId = "corr-fixed-123";
      Response reusedResponse = given()
         .header(CorrelationIdConstants.HEADER_NAME, incomingCorrelationId)
         .when()
         .get("/it/ok")
         .then()
         .statusCode(200)
         .extract()
         .response();

      assertThat(reusedResponse.getHeader(CorrelationIdConstants.HEADER_NAME)).isEqualTo(incomingCorrelationId);
      Map<String, String> reusedBody = reusedResponse.as(new TypeRef<>() {
      });
      assertThat(reusedBody.get(CorrelationIdConstants.MDC_KEY)).isEqualTo(incomingCorrelationId);
   }

   @Test
   void scenarioB_propagatesCorrelationIdToOutboundRestClientCall() {
      Response response = given()
         .header(CorrelationIdConstants.HEADER_NAME, "corr-outbound-1")
         .when()
         .get("/it/call")
         .then()
         .statusCode(200)
         .extract()
         .response();

      assertThat(response.getHeader(CorrelationIdConstants.HEADER_NAME)).isEqualTo("corr-outbound-1");

      Map<String, String> body = response.as(new TypeRef<>() {
      });
      assertThat(body)
         .containsEntry("downstreamCorrelationId", "corr-outbound-1")
         .containsEntry("localCorrelationId", "corr-outbound-1");
   }

   @Test
   void scenarioC_mapsRuntimeAndValidationExceptions() {
      Response runtimeResponse = given()
         .accept(ContentType.JSON)
         .when()
         .get("/it/boom")
         .then()
         .statusCode(500)
         .extract()
         .response();

      String runtimeErrorId = runtimeResponse.getHeader(LoggingConstants.ERROR_ID_HEADER);
      assertThat(runtimeErrorId).isNotBlank();
      ErrorResponse runtimeBody = runtimeResponse.as(ErrorResponse.class);
      assertThat(runtimeBody.errorId()).isEqualTo(runtimeErrorId);
      assertThat(runtimeBody.status()).isEqualTo(500);

      Response validationResponse = given()
         .accept(ContentType.JSON)
         .when()
         .get("/it/validation")
         .then()
         .statusCode(400)
         .extract()
         .response();

      String validationErrorId = validationResponse.getHeader(LoggingConstants.ERROR_ID_HEADER);
      assertThat(validationErrorId).isNotBlank();
      ErrorResponse validationBody = validationResponse.as(ErrorResponse.class);
      assertThat(validationBody.errorId()).isEqualTo(validationErrorId);
      assertThat(validationBody.status()).isEqualTo(400);
   }

   @Test
   void scenarioD_doesNotLeakMdcBetweenRequests() {
      Response firstResponse = given()
         .when()
         .get("/it/ok")
         .then()
         .statusCode(200)
         .extract()
         .response();

      String firstCorrelationId = firstResponse.getHeader(CorrelationIdConstants.HEADER_NAME);
      Map<String, String> firstBody = firstResponse.as(new TypeRef<>() {
      });

      Response secondResponse = given()
         .when()
         .get("/it/ok")
         .then()
         .statusCode(200)
         .extract()
         .response();

      String secondCorrelationId = secondResponse.getHeader(CorrelationIdConstants.HEADER_NAME);
      Map<String, String> secondBody = secondResponse.as(new TypeRef<>() {
      });

      assertThat(firstCorrelationId).isNotBlank();
      assertThat(secondCorrelationId).isNotBlank();
      assertThat(secondCorrelationId).isNotEqualTo(firstCorrelationId);
      assertThat(firstBody.get(CorrelationIdConstants.MDC_KEY)).isEqualTo(firstCorrelationId);
      assertThat(secondBody.get(CorrelationIdConstants.MDC_KEY)).isEqualTo(secondCorrelationId);
   }

   @Test
   void scenarioE_fanoutUsesSameCorrelationIdAndCreatesNewOneForNextRequest() {
      Response firstFanoutResponse = given()
         .when()
         .get("/it/fanout")
         .then()
         .statusCode(200)
         .extract()
         .response();

      String firstCorrelationId = firstFanoutResponse.getHeader(CorrelationIdConstants.HEADER_NAME);
      Map<String, String> firstBody = firstFanoutResponse.as(new TypeRef<>() {
      });
      assertThat(firstBody)
         .containsEntry("downstreamA", firstCorrelationId)
         .containsEntry("downstreamB", firstCorrelationId)
         .containsEntry("localCorrelationId", firstCorrelationId);

      Response secondFanoutResponse = given()
         .when()
         .get("/it/fanout")
         .then()
         .statusCode(200)
         .extract()
         .response();

      String secondCorrelationId = secondFanoutResponse.getHeader(CorrelationIdConstants.HEADER_NAME);
      Map<String, String> secondBody = secondFanoutResponse.as(new TypeRef<>() {
      });
      assertThat(secondCorrelationId).isNotEqualTo(firstCorrelationId);
      assertThat(secondBody)
         .containsEntry("downstreamA", secondCorrelationId)
         .containsEntry("downstreamB", secondCorrelationId)
         .containsEntry("localCorrelationId", secondCorrelationId);

      Response explicitHeaderResponse = given()
         .header(CorrelationIdConstants.HEADER_NAME, "corr-explicit-fanout")
         .when()
         .get("/it/fanout")
         .then()
         .statusCode(200)
         .extract()
         .response();

      Map<String, String> explicitHeaderBody = explicitHeaderResponse.as(new TypeRef<>() {
      });
      assertThat(explicitHeaderResponse.getHeader(CorrelationIdConstants.HEADER_NAME)).isEqualTo("corr-explicit-fanout");
      assertThat(explicitHeaderBody)
         .containsEntry("downstreamA", "corr-explicit-fanout")
         .containsEntry("downstreamB", "corr-explicit-fanout");
   }
}
