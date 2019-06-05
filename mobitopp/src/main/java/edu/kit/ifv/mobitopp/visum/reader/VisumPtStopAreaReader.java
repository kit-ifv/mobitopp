package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
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

  private VisumPtStopArea createPtStopArea(Row row) {
    int id = numberOf(row);
    int stopId = row.valueAsInteger(attribute(StandardAttributes.stationNumber));
    VisumPtStop stop = ptStops.get(stopId);
    String code = codeOf(row);
    String name = nameOf(row);
    VisumNode node = nodeOf(row);
    int type = typeNumberOf(row);
    float xCoord = xCoordOf(row);
    float yCoord = yCoordOf(row);
    return new VisumPtStopArea(id, stop, code, name, node, type, xCoord, yCoord);
  }

  private VisumNode nodeOf(Row row) {
    int nodeId = nodeNumberOf(row);
    VisumNode node = nodes.get(nodeId);
    return node;
  }

}
