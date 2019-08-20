package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.groupingBy;

import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteElement;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;

public class VisumPtLineRouteElementReader extends VisumBaseReader {

  private final Map<String, VisumPtLineRoute> ptLineRoutes;
  private final Map<Integer, VisumPtStopPoint> ptStopPoints;
  private final Map<Integer, VisumNode> nodes;

  public VisumPtLineRouteElementReader(
      NetfileLanguage language, Map<String, VisumPtLineRoute> ptLineRoutes,
      Map<Integer, VisumPtStopPoint> ptStopPoints, Map<Integer, VisumNode> nodes) {
    super(language);
    this.ptLineRoutes = ptLineRoutes;
    this.ptStopPoints = ptStopPoints;
    this.nodes = nodes;
  }

  public Map<VisumPtLineRoute, SortedMap<Integer, VisumPtLineRouteElement>> readElements(
      Stream<Row> content) {
    return content
        .map(this::createElement)
        .collect(groupingBy(e -> e.lineRoute,
            StreamUtils.toSortedMap(e -> e.index, Function.identity())));
  }

  private VisumPtLineRouteElement createElement(Row row) {
    VisumPtLineRoute route = lineRouteOf(row);
    int index = indexOf(row);
    boolean isRoutePoint = row.get(attribute(StandardAttributes.isRoutePoint)).equals("1");
    VisumNode node = nodeOf(row);
    VisumPtStopPoint stopPoint = stopPointOf(row);
    float distance = parseDistance(row.get(attribute(StandardAttributes.toLength)));
    return new VisumPtLineRouteElement(route, index, isRoutePoint, node, stopPoint, distance);
  }

  private VisumNode nodeOf(Row row) {
    String knotNr = row.get(nodeNumber());
    return knotNr.isEmpty() ? null : nodes.get(Integer.valueOf(knotNr));
  }

  private VisumPtStopPoint stopPointOf(Row row) {
    String hpunktNr = row.get(attribute(StandardAttributes.stopNumber));
    return hpunktNr.isEmpty() ? null : ptStopPoints.get(Integer.valueOf(hpunktNr));
  }

  private VisumPtLineRoute lineRouteOf(Row row) {
    return ptLineRoutes.get(lineRouteIdOf(row));
  }

}
