package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumNodeReader extends VisumBaseReader {

  public VisumNodeReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumNode> readNodes(Stream<Row> rows) {
    return rows.collect(toMap(this::numberOf, this::nodeOf));
  }

  private VisumNode nodeOf(Row row) {
    Integer id = numberOf(row);
    String name = nameOf(row);
    int type = typeNumberOf(row);
    float xCoord = xCoordOf(row);
    float yCoord = yCoordOf(row);
    float zCoord = zCoordOf(row);
    return new VisumNode(id, name, type, xCoord, yCoord, zCoord);
  }

  private float zCoordOf(Row row) {
    return row.valueAsFloat(attribute(StandardAttributes.zCoord));
  }

}
