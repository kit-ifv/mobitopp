package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

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

  public VisumConnector createConnector(Row row) {
    VisumZone zone = zones.get(row.valueAsInteger(attribute(StandardAttributes.zoneNumber)));
    VisumNode node = nodes.get(row.valueAsInteger(nodeNumber()));

    String transportSystems = row.get(transportSystemsSet());
    VisumTransportSystemSet systemSet = VisumTransportSystemSet
        .getByCode(transportSystems, allSystems);
    String direction = row.get(direction());
    int type = row.valueAsInteger(typeNumber());
    float distance = parseDistance(row.get(length()));
    int travelTimeInSeconds = parseTime(row.get(travelTimeCarAttribute()));
    return new VisumConnector(zone, node, direction, type, systemSet, distance,
        travelTimeInSeconds);
  }

}
