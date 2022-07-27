package org.acme.producer;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.Record;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.acme.dto.NumberPayload;
import org.acme.dto.Result;
import org.eclipse.microprofile.reactive.messaging.Incoming;
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
  public Multi<KafkaRecord<Integer, NumberPayload>> payload() {
    return Multi.createFrom().item(KafkaRecord.of(payload.getId(), payload));
  }

  @Incoming("result-topic")
  public void newPayload(Result payload) {
  logger.info(payload);
  }
}
