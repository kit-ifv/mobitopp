package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPtStopPointReader extends VisumBaseReader {

  private final Map<Integer, VisumNode> nodes;
  private final Map<Integer, VisumLink> links;
  private final Map<Integer, VisumPtStopArea> ptStopAreas;
  private final VisumTransportSystems allSystems;

  public VisumPtStopPointReader(
      NetfileLanguage language, Map<Integer, VisumNode> nodes, Map<Integer, VisumLink> links,
      Map<Integer, VisumPtStopArea> ptStopAreas, VisumTransportSystems allSystems) {
    super(language);
    this.nodes = nodes;
    this.links = links;
    this.ptStopAreas = ptStopAreas;
    this.allSystems = allSystems;
  }

  public Map<Integer, VisumPtStopPoint> readPtStopPoints(Stream<Row> content) {
    return content.map(this::createStopPoint).collect(toMap(s -> s.id, Function.identity()));
  }

  private VisumPtStopPoint createStopPoint(Row row) {
    int id = row.valueAsInteger(number());
    VisumTransportSystemSet systemSet = transportSystemsOf(row);
    VisumPtStopArea stopArea = stopAreaOf(row);
    String code = row.get(code());
    String name = row.get(name());
    int type = row.valueAsInteger(typeNumber());
    boolean directed = row.valueAsInteger(directed()) == 1;
    if (!row.get(nodeNumber()).isEmpty()) {
      VisumNode node = atNodeOf(row);
      return new VisumPtStopPoint.Node(id, stopArea, code, name, type, systemSet, directed, node);
    }
    VisumNode nodeBefore = fromNodeOf(row);
    VisumLink link = linkOf(row);
    float relativePosition = row.valueAsFloat(attribute(StandardAttributes.relativePosition));
    return new VisumPtStopPoint.Link(id, stopArea, code, name, type, systemSet, directed,
        nodeBefore, link, relativePosition);
  }

  private VisumPtStopArea stopAreaOf(Row row) {
    Integer areaId = row.valueAsInteger(attribute(StandardAttributes.stopAreaNumber));
    return ptStopAreas.get(areaId);
  }

  private VisumTransportSystemSet transportSystemsOf(Row row) {
    String transportSystems = row.get(transportSystemsSet());
    return VisumTransportSystemSet.getByCode(transportSystems, allSystems);
  }

  VisumNode atNodeOf(Row row) {
    Integer nodeId = row.valueAsInteger(nodeNumber());
    return nodes.get(nodeId);
  }

  private VisumNode fromNodeOf(Row row) {
    Integer nodeId = row.valueAsInteger(fromNode());
    return nodes.get(nodeId);
  }

  private VisumLink linkOf(Row row) {
    Integer linkId = row.valueAsInteger(attribute(StandardAttributes.linkNumber));
    return links.get(linkId);
  }

}
