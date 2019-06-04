package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPtTimeProfileElementReader extends VisumBaseReader {

  private final Map<String, VisumPtLineRoute> ptLineRoutes;

  public VisumPtTimeProfileElementReader(
      NetfileLanguage language, Map<String, VisumPtLineRoute> ptLineRoutes) {
    super(language);
    this.ptLineRoutes = Collections.unmodifiableMap(ptLineRoutes);
  }

  public Map<String, Map<Integer, VisumPtTimeProfileElement>> readElements(Stream<Row> content) {
    return content
        .map(this::createElement)
        .collect(groupingBy(e -> e.profileId, toMap(e -> e.index, Function.identity())));
  }

  private VisumPtTimeProfileElement createElement(Row row) {
    String profileName = row.get(timeProfileName());
    String lineId = lineIdOf(row);
    String profileId = lineId + ";" + profileName;
    VisumPtLineRoute route = ptLineRoutes.get(lineId);
    int index = row.valueAsInteger(index());
    int lrelemindex = row.valueAsInteger(attribute(StandardAttributes.lineRouteElementIndex));
    boolean leave = row.get(attribute(StandardAttributes.getOff)).equals("1");
    boolean enter = row.get(attribute(StandardAttributes.board)).equals("1");
    int arrival = parseTimeAsSeconds(row.get(attribute(StandardAttributes.arrival)));
    int departure = parseTimeAsSeconds(row.get(departure()));
    VisumPtLineRouteElement lineRouteElement = route.getElements().get(lrelemindex);
    return new VisumPtTimeProfileElement(route, profileId, profileName, index, lrelemindex,
        lineRouteElement, leave, enter, arrival, departure);
  }

  private String lineIdOf(Row row) {
    String lineName = row.get(lineName());
    String lineRouteName = row.get(lineRouteName());
    String lineRouteDirection = row.get(directionCode());
    return lineName + ";" + lineRouteName + ";" + lineRouteDirection;
  }

}
