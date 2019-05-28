package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumFaceReader extends VisumBaseReader {

  private final Map<Integer, VisumEdge> lines;

  public VisumFaceReader(NetfileLanguage language, Map<Integer, VisumEdge> lines) {
    super(language);
    this.lines = lines;
  }

  public Map<Integer, VisumFace> readFaces(Stream<Row> content) {
    return content
        .map(this::createRingElement)
        .collect(groupingBy(r -> r.id, TreeMap::new, toMap(r -> r.idx, r -> r)))
        .entrySet()
        .stream()
        .map(e -> createFace(e.getKey(), e.getValue()))
        .collect(toMap(f -> f.id, Function.identity()));
  }

  public VisumFace createFace(Integer id, Map<Integer, RingElement> elements) {
    List<VisumEdge> edges = elements
        .values()
        .stream()
        .map(r -> r.line_id)
        .map(lines::get)
        .collect(toList());
    List<Integer> direction = elements.values().stream().map(r -> r.direction).collect(toList());
    return new VisumFace(id, edges, direction);
  }

  public RingElement createRingElement(Row row) {
    int id = row.valueAsInteger(ringId());
    int index = row.valueAsInteger(index());
    int lineId = row.valueAsInteger(edgeId());
    int direction = row.valueAsInteger(direction());
    return new RingElement(id, index, lineId, direction);
  }

  private static final class RingElement {

    private final int line_id;
    private final int direction;
    private final int idx;
    private final int id;

    public RingElement(int id, int idx, int line_id, int direction) {
      super();
      this.id = id;
      this.idx = idx;
      this.line_id = line_id;
      this.direction = direction;
    }
  }

}
