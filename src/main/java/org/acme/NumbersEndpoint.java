package org.acme;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.acme.dto.NumberPayload;
import org.acme.producer.PayloadService;

@Path("/numberPayload")
@Produces("application/json")
@Consumes("application/json")
public class NumbersEndpoint {
  @Inject
  PayloadService payloadService;

  @GET
  public List<NumberPayload> list() {
    return payloadService.listNumberPayload();
  }

  @POST
  public List<NumberPayload> add(NumberPayload numberPayload) {
    payloadService.addNumberPayloadToMongo(numberPayload);
    return payloadService.listNumberPayload();
  }
}
