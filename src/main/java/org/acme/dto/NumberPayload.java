package org.acme.dto;

import java.util.Date;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberPayload {

  Timestamp timestamp;
  Double[] numbersList;
  Integer id;

  public NumberPayload(Integer id, Double[] numberList) {
    Date date = new Date();
    this.id = id;
    this.numbersList = numberList;
    this.timestamp = new Timestamp(date.getTime());
  }
}
