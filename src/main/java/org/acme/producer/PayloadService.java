package org.acme.producer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.acme.dto.NumberPayload;
import org.acme.dto.Result;
import org.acme.dto.ResultPayload;
import org.bson.Document;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PayloadService {

  @Inject
  Logger logger;

  @Inject
  MongoClient mongoClient;

  private Random random = new Random();
  private final NumberPayload payload = generatePayload();

  private NumberPayload generatePayload() {
    NumberPayload numberPayload = new NumberPayload();
    List<Double> numbersList = new ArrayList<>();

    for (int i = 0; i < 50000; i++) {
      numbersList.add(i, 0 + random.nextDouble() * 1000000);
    }
    numberPayload.setId(1);
    numberPayload.setNumbersList(numbersList);
    Date date = new Date();
    numberPayload.setTimestamp(new Timestamp(date.getTime()));
    return numberPayload;
  }

  @Outgoing("numbers-payload")
  public Multi<KafkaRecord<Integer, NumberPayload>> payload() {
    addNumberPayloadToMongo(payload);
    return Multi.createFrom().item(KafkaRecord.of(payload.getId(), payload));
  }

  @Incoming("result-topic")
  public void newPayload(ResultPayload payload) {
    logger.info("Received payload: " + payload);
    addResultPayloadToMongo(payload);
  }

  public List<ResultPayload> listResultPayload() {
    List<ResultPayload> list = new ArrayList<>();
    try (MongoCursor<Document> cursor = getCollection("resultsPayload").find().iterator()) {
      while (cursor.hasNext()) {
        Document document = cursor.next();
        ResultPayload resultPayload = new ResultPayload();
        resultPayload.setId(document.getInteger("id"));
        resultPayload.setTimestamp(Timestamp.valueOf(document.getString("timestamp")));
        resultPayload.setNumbers(document.getList("numbers", Double.class));
        Document resultsDocument = (Document) document.get("result");
        resultPayload.setResult((Result) resultsDocument.get("result"));
        list.add(resultPayload);
      }
    }
    return list;
  }

  public List<NumberPayload> listNumberPayload() {
    List<NumberPayload> list = new ArrayList<>();
    try (MongoCursor<Document> cursor = getCollection("numberPayloads").find().iterator()) {
      while (cursor.hasNext()) {
        Document document = cursor.next();
        NumberPayload numberPayload = new NumberPayload();
        numberPayload.setTimestamp(Timestamp.valueOf(document.getString("timestamp")));
        numberPayload.setNumbersList(document.getList("numbers", Double.class));
        numberPayload.setId(document.getInteger("id"));
        list.add(numberPayload);
      }
    }
    return list;
  }

  public void addNumberPayloadToMongo(NumberPayload payload) {
    logger.info("Timestamp NumberPayload before Mongo: " + payload.getTimestamp().toString());
    Document document = new Document()
        .append("id", payload.getId())
        .append("numbers", payload.getNumbersList())
        .append("timestamp", payload.getTimestamp().toString());
    getCollection("numberPayloads").insertOne(document);
  }

  public void addResultPayloadToMongo(ResultPayload payload) {
    logger.info("Timestamp ResultPayload before Mongo: " + payload.getTimestamp().toString());
    Document document = new Document()
        .append("id", payload.getId())
        .append("numbers", payload.getNumbers())
        .append("timestamp", payload.getTimestamp().toString())
        .append("result", payload.getResult());
    getCollection("resultsPayload").insertOne(document);
  }

  private MongoCollection<Document> getCollection(String collectionName) {
    return mongoClient.getDatabase("PerformanceAnalysis").getCollection(collectionName);
  }
}
