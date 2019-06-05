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
    String lineId = lineRouteIdOf(row);
    String profileName = nameOf(row);
    String profileId = lineId + ";" + profileName;
    VisumPtLineRoute route = ptLineRoutes.get(lineId);
    Map<Integer, VisumPtTimeProfileElement> routeElements = elements.get(profileId);
    return new VisumPtTimeProfile(profileId, profileName, route, routeElements);
  }

}
