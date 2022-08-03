package org.acme;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;
import org.acme.producer.PayloadService;

@Path("configure")
@Produces("application/json")
@Consumes("application/json")
@Slf4j
public class ConfigurationEndpoint {
  @Inject
  PayloadService payloadService;

  @Path("numberPayloadSize/{count}")
  @POST
  public void configureNumberPayloadSize(Integer count) {
    log.info("Sending {} numberPayload(s) to the consumer", count);
    //payloadService.configureNumberPayloadSize(count);
  }

  @Path("clearDatabase")
  @POST
  public void clearDatabase() {
    payloadService.clearDatabase();
  }
}
