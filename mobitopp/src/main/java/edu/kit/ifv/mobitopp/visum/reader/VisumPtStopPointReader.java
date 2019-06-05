package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
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
    int id = numberOf(row);
    VisumTransportSystemSet systemSet = transportSystemsOf(row, allSystems);
    VisumPtStopArea stopArea = stopAreaOf(row);
    String code = codeOf(row);
    String name = nameOf(row);
    int type = typeNumberOf(row);
    boolean directed = row.valueAsInteger(directed()) == 1;
    if (isNode(row)) {
      VisumNode node = atNodeOf(row);
      return new VisumPtStopPoint.Node(id, stopArea, code, name, type, systemSet, directed, node);
    }
    VisumNode nodeBefore = nodes.get(fromNodeOf(row));
    VisumLink link = linkOf(row);
    float relativePosition = row.valueAsFloat(attribute(StandardAttributes.relativePosition));
    return new VisumPtStopPoint.Link(id, stopArea, code, name, type, systemSet, directed,
        nodeBefore, link, relativePosition);
  }

  private String directed() {
    return attribute(StandardAttributes.directed);
  }

  private boolean isNode(Row row) {
    return !row.get(nodeNumber()).isEmpty();
  }

  private VisumPtStopArea stopAreaOf(Row row) {
    Integer areaId = row.valueAsInteger(attribute(StandardAttributes.stopAreaNumber));
    return ptStopAreas.get(areaId);
  }

  private VisumNode atNodeOf(Row row) {
    int nodeId = nodeNumberOf(row);
    return nodes.get(nodeId);
  }

  private VisumLink linkOf(Row row) {
    Integer linkId = row.valueAsInteger(attribute(StandardAttributes.linkNumber));
    return links.get(linkId);
  }

}
