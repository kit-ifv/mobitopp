package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.VisumPoint;

public class VisumPointReader extends VisumBaseReader {

  public VisumPointReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumPoint> readPoints(Stream<Row> content) {
    return content.collect(toMap(this::idOf, this::createPoint));
  }

  private VisumPoint createPoint(Row row) {
    float x = xCoordOf(row);
    float y = yCoordOf(row);
    return new VisumPoint(x, y);
  }

}
