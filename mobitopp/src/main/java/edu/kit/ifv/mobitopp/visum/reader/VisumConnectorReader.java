package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.Row;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class VisumConnectorReader extends VisumBaseReader {

  private final Map<Integer, VisumNode> nodes;
  private final Map<Integer, VisumZone> zones;
  private final VisumTransportSystems allSystems;

  public VisumConnectorReader(
      NetfileLanguage language, Map<Integer, VisumNode> nodes, Map<Integer, VisumZone> zones,
      VisumTransportSystems allSystems) {
    super(language);
    this.nodes = nodes;
    this.zones = zones;
    this.allSystems = allSystems;
  }

  public Map<Integer, List<VisumConnector>> readConnectors(Stream<Row> content) {
    return content.map(this::createConnector).collect(groupingBy(c -> c.zone.id));
  }

  private VisumConnector createConnector(Row row) {
    VisumZone zone = zones.get(row.valueAsInteger(attribute(StandardAttributes.zoneNumber)));
    VisumNode node = nodes.get(nodeNumberOf(row));

    VisumTransportSystemSet systemSet = transportSystemsOf(row, allSystems);
    String direction = directionOf(row);
    int type = typeNumberOf(row);
    float distance = lengthOf(row);
    int travelTimeInSeconds = travelTimeCarOf(row);
    return new VisumConnector(zone, node, direction, type, systemSet, distance,
        travelTimeInSeconds);
  }

  private Integer travelTimeCarOf(Row row) {
    return parseTime(row.get(travelTimeCarAttribute()));
  }

  private String travelTimeCarAttribute() {
    return attribute(StandardAttributes.travelTimeCar);
  }

  private String directionOf(Row row) {
    return row.get(direction());
  }

}
