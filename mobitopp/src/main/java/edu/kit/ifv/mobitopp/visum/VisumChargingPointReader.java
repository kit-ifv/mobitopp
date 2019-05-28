package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumChargingPointReader extends VisumBaseReader {

  public VisumChargingPointReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumChargingPoint> readPoints(Stream<Row> content) {
    return content
        .filter(this::elementHasPower)
        .map(this::createChargingPoint)
        .collect(toMap(c -> c.id, Function.identity()));
  }

  private boolean elementHasPower(Row row) {
    return !row.get(power()).isEmpty();
  }

  private VisumChargingPoint createChargingPoint(Row row) {
    int id = row.valueAsInteger(number());
    float xCoord = row.valueAsFloat(xCoord());
    float yCoord = row.valueAsFloat(yCoord());
    String station = row.get(id());
    int stationId = Double.valueOf(station.isEmpty() ? "0" : station).intValue();
    float power = Float.parseFloat(row.get(power()));
    return new VisumChargingPoint(id, xCoord, yCoord, stationId, power);
  }

}
