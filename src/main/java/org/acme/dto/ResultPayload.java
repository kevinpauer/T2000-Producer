package org.acme.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPayload {
  Integer id;
  Double[] numbers;
  Timestamp timestamp;
  Result[] results;

  public ResultPayload(Integer id, Double[] numbers, Timestamp timestamp, Result[] results) {
    this.id = id;
    this.numbers = numbers;
    this.timestamp = timestamp;
    this.results = results;
  }
}
