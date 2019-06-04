package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumLinkReader extends VisumBaseReader {

  private final Map<Integer, VisumNode> nodes;
  private final VisumTransportSystems allSystems;
  private final VisumLinkTypes linkTypes;

  public VisumLinkReader(
      NetfileLanguage language, Map<Integer, VisumNode> nodes, VisumTransportSystems allSystems,
      VisumLinkTypes linkTypes) {
    super(language);
    this.nodes = nodes;
    this.allSystems = allSystems;
    this.linkTypes = linkTypes;
  }

  public Map<Integer, VisumLink> readLinks(Stream<Row> content) {
    Map<Integer, VisumLink> data = new HashMap<>();
    List<Row> rows = content.collect(toList());
    for (int i = 0; i < rows.size() - 1; i += 2) {
      Row first = rows.get(i);
      Row second = rows.get(i + 1);
      VisumLink tmp = createLink(first, second);
      data.put(tmp.id, tmp);
    }
    return data;
  }

  private VisumLink createLink(Row first, Row second) {
    int id = first.valueAsInteger(number());
    VisumOrientedLink link1 = readOrientedLink("" + id + ":1", first);
    VisumOrientedLink link2 = readOrientedLink("" + id + ":2", second);
    return new VisumLink(id, link1, link2);
  }

  private VisumOrientedLink readOrientedLink(String id, Row row) {
    VisumNode fromNode = fromNodeOf(row);
    VisumNode toNode = toNodeOf(row);
    String name = row.get(name());
    VisumLinkType linkType = linkTypeOf(row);
    VisumTransportSystemSet systemSet = transportSystemOf(row);
    float distance = parseDistance(row.get(length()));
    int numberOfLanes = row.valueAsInteger(numberOfLanes());
    int capacity = row.valueAsInteger(capacityCar());
    int speed = parseSpeed(row.get(freeFlowSpeedCar()), language);
    int walkSpeed = walkSpeed(row, language);
    VisumLinkAttributes attributes = new VisumLinkAttributes(numberOfLanes, capacity, speed,
        walkSpeed);
    return new VisumOrientedLink(id, fromNode, toNode, name, linkType, systemSet, distance,
        attributes);
  }

  VisumNode fromNodeOf(Row row) {
    VisumNode fromNode = nodes.get(row.valueAsInteger(fromNode()));
    return fromNode;
  }

  VisumNode toNodeOf(Row row) {
    VisumNode toNode = nodes.get(row.valueAsInteger(toNode()));
    return toNode;
  }

  VisumLinkType linkTypeOf(Row row) {
    VisumLinkType linkType = linkTypes.getById(row.valueAsInteger(typeNumber()));
    return linkType;
  }

  VisumTransportSystemSet transportSystemOf(Row row) {
    String transportSystems = row.get(transportSystemsSet());
    VisumTransportSystemSet systemSet = VisumTransportSystemSet
        .getByCode(transportSystems, allSystems);
    return systemSet;
  }

  private int walkSpeed(Row row, NetfileLanguage language) {
    return VisumNetworkReader.walkSpeed(row, language);
  }
}
