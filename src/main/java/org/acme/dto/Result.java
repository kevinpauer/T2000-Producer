package org.acme.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
  String consumer;
  Timestamp publishedTimestamp;
  Boolean isCorrect;

  public Result(String consumer, Timestamp publishedTimestamp, Boolean isCorrect) {
    this.consumer = consumer;
    this.publishedTimestamp = publishedTimestamp;
    this.isCorrect = isCorrect;
  }
}
