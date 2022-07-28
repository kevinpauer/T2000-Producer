package org.acme.dto;

import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberPayload {

  Timestamp timestamp;
  List<Double> numbersList;
  Integer id;
}
