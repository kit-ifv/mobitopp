package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.VisumPtLine;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteDirection;
import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPtLineRouteReader extends VisumBaseReader {

  private final Map<String, VisumPtLine> ptLines;
  private final Function<String, VisumPtLineRouteDirection> converter;

  public VisumPtLineRouteReader(
      NetfileLanguage language, Map<String, VisumPtLine> ptLines,
      Function<String, VisumPtLineRouteDirection> converter) {
    super(language);
    this.ptLines = ptLines;
    this.converter = converter;
  }

  public Map<String, VisumPtLineRoute> readPtLineRoutes(Stream<Row> content) {
    return content.map(this::createLineRoute).collect(toMap(l -> l.id, Function.identity()));
  }

  private VisumPtLineRoute createLineRoute(Row row) {
    String lineName = lineNameOf(row);
    String lineRouteName = nameOf(row);
    String lineRouteDirection = directionCodeOf(row);
    String id = lineName + ";" + lineRouteName + ";" + lineRouteDirection;
    VisumPtLineRouteDirection direction = converter.apply(lineRouteDirection);
    VisumPtLine line = ptLines.get(lineName);
    return new VisumPtLineRoute(id, line, lineRouteName, direction);
  }

  private String directionCodeOf(Row row) {
    return row.get(directionCode());
  }

  private String lineNameOf(Row row) {
    return row.get(lineName());
  }

}
