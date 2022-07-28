package org.acme;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.acme.dto.Result;
import org.acme.dto.ResultPayload;
import org.acme.producer.PayloadService;

@Path("/results")
@Produces("application/json")
@Consumes("application/json")
public class ResultEndpoint {
  @Inject
  PayloadService payloadService;

  @GET
  public List<ResultPayload> list() {
    return payloadService.list();
  }

  @POST
  public List<ResultPayload> add(ResultPayload result) {
    payloadService.addResultPayloadToMongo(result);
    return payloadService.list();
  }
}
