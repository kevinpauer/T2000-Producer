package org.acme.dto;

import java.util.Date;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberPayload {

  Timestamp timestamp;
  Double number1;
  Double number2;
  Integer id;

  public NumberPayload(Integer id, Double number1, Double number2) {
    Date date = new Date();
    this.id = id;
    this.number1 = number1;
    this.number2 = number2;
    this.timestamp = new Timestamp(date.getTime());
  }
}
