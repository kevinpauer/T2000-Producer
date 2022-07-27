package org.acme.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
  Timestamp publishedTimestamp;
  Boolean isCorrect;

  public Result(Timestamp publishedTimestamp, Boolean isCorrect) {
    this.publishedTimestamp = publishedTimestamp;
    this.isCorrect = isCorrect;
  }
}
