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
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
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
  private NumberPayload payload;

  private NumberPayload generatePayload(Integer id) {
    NumberPayload numberPayload = new NumberPayload();
    List<Double> numbersList = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      numbersList.add(0 + random.nextDouble() * 1000000);
    }
    numberPayload.setId(id);
    numberPayload.setNumbersList(numbersList);
    Date date = new Date();
    numberPayload.setTimestamp(new Timestamp(date.getTime()));
    numberPayload.setPayloadSize(10);
    return numberPayload;
  }

  @Outgoing("numbers-payload")
  public Multi<KafkaRecord<Integer, NumberPayload>> payload() {
    clearDatabase();
    payload = generatePayload(0);
    addNumberPayloadToMongo(payload);
    logger.info("Sending numberPayload to Consumer. " + payload.getTimestamp());
    return Multi.createFrom().item(KafkaRecord.of(payload.getId(), payload));
  }

  @Incoming("result-topic")
  @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
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
        Result result = new Result(document.getString("consumer"),
            Timestamp.valueOf(document.getString("publishedTimestamp")),
            document.getBoolean("isCorrect"));
        resultPayload.setResult(result);
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
        numberPayload.setPayloadSize(document.getInteger("payloadSize"));
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
        .append("timestamp", payload.getTimestamp().toString())
        .append("payloadSize", payload.getPayloadSize());
    getCollection("numberPayloads").insertOne(document);
  }

  public void addResultPayloadToMongo(ResultPayload payload) {
    logger.info("Timestamp ResultPayload before Mongo: " + payload.getResult().getPublishedTimestamp());
    Document document = new Document()
        .append("id", payload.getId())
        .append("numbers", payload.getNumbers())
        .append("timestamp", payload.getTimestamp().toString())
        .append("consumer", payload.getResult().getConsumer())
        .append("publishedTimestamp", payload.getResult().getPublishedTimestamp().toString())
        .append("isCorrect", payload.getResult().getIsCorrect());
    getCollection("resultsPayload").insertOne(document);
  }

  private MongoCollection<Document> getCollection(String collectionName) {
    return mongoClient.getDatabase("PerformanceAnalysis").getCollection(collectionName);
  }

  public void clearDatabase() {
    logger.info("Database cleared!");
    mongoClient.getDatabase("PerformanceAnalysis").drop();
  }
}

//private boolean checkIfEntryExistsInDB(ResultPayload payload) {
//  Document document = new Document()
//      .append("id", payload.getId())
//      .append("numbers", payload.getNumbers())
//      .append("timestamp", payload.getTimestamp().toString())
//      .append("result", payload.getResult());
//  long count = mongoClient.getDatabase("PerformanceAnalysis").getCollection("resultsPayload")
//      .countDocuments(document);
//
//  if (count == 0) {
//    return false;
//  } else {
//    return true;
//  }
//}