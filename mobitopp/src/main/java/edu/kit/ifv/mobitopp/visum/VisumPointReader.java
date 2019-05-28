package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPointReader extends VisumBaseReader {

  public VisumPointReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumPoint> readPoints(Stream<Row> content) {
    return content.collect(toMap(this::id, this::createPoint));
  }

  public int id(Row row) {
    return row.valueAsInteger(id());
  }

  public VisumPoint createPoint(Row row) {
    float x = row.valueAsFloat(xCoord());
    float y = row.valueAsFloat(yCoord());
    return new VisumPoint(x, y);
  }

}
