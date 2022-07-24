package org.acme.producer;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.acme.dto.NumberPayload;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PayloadProducer {

  @Inject
  Logger logger;

  private Random random = new Random();
  private List<NumberPayload> payload = generatePayload();

  private List<NumberPayload> generatePayload() {
    List<NumberPayload> payloadList = new ArrayList<>();

    for (int i = 0; i < 1000; i++) {
      double randomNumber1 = 0 + random.nextDouble() * 1000000;
      double randomNumber2 = 0 + random.nextDouble() * 1000000;
      payloadList.add(new NumberPayload(i + 1, randomNumber1, randomNumber2));
    }

    return payloadList;
  }

  // Populates movies into Kafka topic
  @Outgoing("numbers-payload")
  public Multi<Record<Integer, NumberPayload>> payload() {
    logger.info("Adding payload of size " + payload.size());
    return Multi.createFrom().items(payload.stream()
        .map(numberPayload -> Record.of(numberPayload.getId(), numberPayload))
    );
  }
}
