package edu.kit.ifv.mobitopp.visum;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumEdgeReader extends VisumBaseReader {

  private final Map<Integer, VisumPoint> points;
  private final Map<Integer, SortedMap<Integer, VisumPoint>> intermediatePoints;

  public VisumEdgeReader(
      NetfileLanguage language, Map<Integer, VisumPoint> points,
      Map<Integer, SortedMap<Integer, VisumPoint>> intermediatePoints) {
    super(language);
    this.points = points;
    this.intermediatePoints = intermediatePoints;
  }

  public Map<Integer, VisumEdge> readEdges(Stream<Row> content) {
    return content.map(this::createEdge).collect(toMap(e -> e.id, Function.identity()));
  }

  public VisumEdge createEdge(Row row) {
    int edgeId = row.valueAsInteger(id());
    int fromId = row.valueAsInteger(attribute(StandardAttributes.fromPointId));
    int toId = row.valueAsInteger(attribute(StandardAttributes.toPointId));

    VisumPoint from = points.get(fromId);
    VisumPoint to = points.get(toId);

    List<VisumPoint> intermediate = intermediatePoints.containsKey(edgeId)
        ? new ArrayList<>(intermediatePoints.get(edgeId).values())
        : emptyList();

    assert from != null;
    assert to != null;

    return new VisumEdge(edgeId, from, to, intermediate);
  }

}
