package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPtStationReader extends VisumBaseReader {

  public VisumPtStationReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumPtStop> readPtStops(Stream<Row> content) {
    return content.map(this::createPtStop).collect(toMap(s -> s.id, Function.identity()));
  }

  private VisumPtStop createPtStop(Row row) {
    int id = row.valueAsInteger(number());
    String code = row.get(code());
    String name = row.get(name());
    Integer type = row.valueAsInteger(typeNumber());
    float xCoord = row.valueAsFloat(xCoord());
    float yCoord = row.valueAsFloat(yCoord());
    return new VisumPtStop(id, code, name, type, xCoord, yCoord);
  }

}
