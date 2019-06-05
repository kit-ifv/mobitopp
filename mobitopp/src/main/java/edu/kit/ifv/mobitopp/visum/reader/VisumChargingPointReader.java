package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumChargingPoint;
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
    int id = numberOf(row);
    float xCoord = xCoordOf(row);
    float yCoord = yCoordOf(row);
    String station = stationIdOf(row);
    int stationId = Double.valueOf(station.isEmpty() ? "0" : station).intValue();
    float power = row.valueAsFloat(power());
    return new VisumChargingPoint(id, xCoord, yCoord, stationId, power);
  }

  private String stationIdOf(Row row) {
    return row.get(id());
  }

  private String power() {
    return attribute(StandardAttributes.power);
  }

}
