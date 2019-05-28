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
    return rows.collect(toMap(this::idOf, this::nodeOf));
  }

  public int idOf(Row row) {
    return row.valueAsInteger(number());
  }

  private VisumNode nodeOf(Row row) {
    Integer id = idOf(row);
    String name = row.get(name());
    int type = row.valueAsInteger(typeNumber());
    float xCoord = row.valueAsFloat(xCoord());
    float yCoord = row.valueAsFloat(yCoord());
    float zCoord = row.valueAsFloat(zCoord());
    return new VisumNode(id, name, type, xCoord, yCoord, zCoord);
  }

  private String zCoord() {
    return attribute(StandardAttributes.zCoord);
  }

}
