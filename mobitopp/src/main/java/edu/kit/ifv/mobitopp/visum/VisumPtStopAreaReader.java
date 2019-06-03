package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPtStopAreaReader extends VisumBaseReader {

  private final Map<Integer, VisumNode> nodes;
  private final Map<Integer, VisumPtStop> ptStops;

  public VisumPtStopAreaReader(
      NetfileLanguage language, Map<Integer, VisumNode> nodes, Map<Integer, VisumPtStop> ptStops) {
    super(language);
    this.nodes = nodes;
    this.ptStops = ptStops;
  }

  public Map<Integer, VisumPtStopArea> readPtStopAreas(Stream<Row> content) {
    return content.map(this::createPtStopArea).collect(toMap(s -> s.id, Function.identity()));
  }

  VisumPtStopArea createPtStopArea(Row row) {
    int id = row.valueAsInteger(number());
    int stopId = row.valueAsInteger(attribute(StandardAttributes.stationNumber));
    int nodeId = row.valueAsInteger(nodeNumber());
    VisumPtStop stop = ptStops.get(stopId);
    String code = row.get(code());
    String name = row.get(name());
    VisumNode node = nodes.get(nodeId);
    int type = row.valueAsInteger(typeNumber());
    float xCoord = row.valueAsFloat(xCoord());
    float yCoord = row.valueAsFloat(yCoord());
    return new VisumPtStopArea(id, stop, code, name, node, type, xCoord, yCoord);
  }

}
