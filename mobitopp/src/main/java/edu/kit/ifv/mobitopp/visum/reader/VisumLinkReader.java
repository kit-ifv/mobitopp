package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumLinkAttributes;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumLinkTypes;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;

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
    int id = numberOf(first);
    VisumOrientedLink link1 = readOrientedLink("" + id + ":1", first);
    VisumOrientedLink link2 = readOrientedLink("" + id + ":2", second);
    return new VisumLink(id, link1, link2);
  }

  private VisumOrientedLink readOrientedLink(String id, Row row) {
    VisumNode fromNode = nodes.get(fromNodeOf(row));
    VisumNode toNode = nodes.get(toNodeOf(row));
    String name = nameOf(row);
    VisumLinkType linkType = linkTypeOf(row);
    VisumTransportSystemSet systemSet = transportSystemsOf(row, allSystems);
    float distance = lengthOf(row);
    int numberOfLanes = numberOfLanesOf(row);
    int capacity = capacityCarOf(row);
    int speed = freeFlowSpeedOf(row);
    int walkSpeed = walkSpeedOf(row);
    VisumLinkAttributes attributes = new VisumLinkAttributes(numberOfLanes, capacity, speed,
        walkSpeed);
    return new VisumOrientedLink(id, fromNode, toNode, name, linkType, systemSet, distance,
        attributes);
  }

  private VisumLinkType linkTypeOf(Row row) {
    return linkTypes.getById(typeNumberOf(row));
  }
}
