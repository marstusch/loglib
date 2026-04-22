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
class LoggingExtensionConsumerIntegrationTest {

   @Test
   void soll_correlationid_generieren_und_wiederverwenden_und_mdc_felder_setzen() {
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
         .containsEntry(LoggingConstants.ENVIRONMENT_KEY, "test");

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
   void soll_correlationid_an_outbound_restclient_weitergeben() {
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
   void soll_bewusst_geworfene_http_fehler_unveraendert_lassen() {
      Response response = given()
         .accept(ContentType.JSON)
         .when()
         .get("/it/http/forbidden")
         .then()
         .statusCode(403)
         .extract()
         .response();

      assertThat(response.getHeader(LoggingConstants.ERROR_ID_HEADER)).isNull();
   }

   @Test
   void soll_framework_404_fuer_unbekannten_pfad_nicht_als_errorresponse_umdeuten() {
      Response response = given()
         .accept(ContentType.JSON)
         .when()
         .get("/")
         .then()
         .statusCode(404)
         .extract()
         .response();

      assertThat(response.getHeader(LoggingConstants.ERROR_ID_HEADER)).isNull();
   }

   @Test
   void soll_downstream_http_fehlerstatus_fuer_aufrufenden_client_unveraendert_lassen() {
      Response response = given()
         .accept(ContentType.JSON)
         .when()
         .get("/it/call-missing")
         .then()
         .statusCode(500)
         .extract()
         .response();

      assertThat(response.getHeader(LoggingConstants.ERROR_ID_HEADER)).isNull();
   }

   @Test
   void soll_mdc_zwischen_requests_nicht_leaken() {
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
   void soll_beim_fanout_selbe_correlationid_nutzen_und_fuer_naechsten_request_neu_generieren() {
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
