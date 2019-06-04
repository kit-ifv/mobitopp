package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPtTimeProfileReader extends VisumBaseReader {

  private final Map<String, VisumPtLineRoute> ptLineRoutes;
  private final Map<String, Map<Integer, VisumPtTimeProfileElement>> elements;

  public VisumPtTimeProfileReader(
      NetfileLanguage language, Map<String, VisumPtLineRoute> ptLineRoutes,
      Map<String, Map<Integer, VisumPtTimeProfileElement>> elements) {
    super(language);
    this.ptLineRoutes = ptLineRoutes;
    this.elements = elements;
  }

  public Map<String, VisumPtTimeProfile> readProfile(Stream<Row> content) {
    return content.map(this::createProfile).collect(toMap(p -> p.profileId, Function.identity()));
  }

  private VisumPtTimeProfile createProfile(Row row) {
    String lineId = lineIdOf(row);
    String profileName = row.get(name());
    String profileId = lineId + ";" + profileName;
    VisumPtLineRoute route = ptLineRoutes.get(lineId);
    Map<Integer, VisumPtTimeProfileElement> routeElements = elements.get(profileId);
    VisumPtTimeProfile profile = new VisumPtTimeProfile(profileId, profileName, route,
        routeElements);
    return profile;
  }

  private String lineIdOf(Row row) {
    String lineName = row.get(lineName());
    String lineRouteName = row.get(lineRouteName());
    String lineRouteDirection = row.get(directionCode());
    return lineName + ";" + lineRouteName + ";" + lineRouteDirection;
  }

}
