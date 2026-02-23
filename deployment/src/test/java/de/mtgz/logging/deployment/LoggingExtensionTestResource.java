package de.mtgz.logging.deployment;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/log-test")
public class LoggingExtensionTestResource {

   private static final Logger LOG = Logger.getLogger(LoggingExtensionTestResource.class);

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> test() {
      LOG.info("logging extension smoke test");
      return Map.of(
         "jsonEnabled", ConfigProvider.getConfig().getValue("quarkus.log.console.json", String.class),
         "serviceField", ConfigProvider.getConfig()
            .getValue("quarkus.log.console.json.additional-field.service.value", String.class),
         "environmentField", ConfigProvider.getConfig()
            .getValue("quarkus.log.console.json.additional-field.environment.value", String.class),
         "correlationId", read("correlationId"),
         "httpMethod", read("http.method"),
         "httpPath", read("http.path"),
         "traceId", read("traceId"),
         "spanId", read("spanId"));
   }

   private static String read(String key) {
      Object value = MDC.get(key);
      return value == null ? "" : value.toString();
   }
}
