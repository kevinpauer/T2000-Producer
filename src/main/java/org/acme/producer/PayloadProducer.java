package org.acme.producer;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.KafkaAdmin;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
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
  private final NumberPayload payload = generatePayload();

  private NumberPayload generatePayload() {
    Double[] numbersList = new Double[50000];

    for (int i = 0; i < 50000; i++) {
      numbersList[i] = 0 + random.nextDouble() * 1000000;
    }

    return new NumberPayload(1, numbersList);
  }

  // Populates movies into Kafka topic
  @Outgoing("numbers-payload")
  public Multi<Record<Integer, NumberPayload>> payload() {
    return Multi.createFrom().item(Record.of(payload.getId(), payload));
  }
}
