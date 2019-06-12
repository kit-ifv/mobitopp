package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.Row;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;

public class VisumPtVehicleJourneyReader extends VisumBaseReader {

  private final Map<String, VisumPtLineRoute> ptLineRoutes;
  private final Map<String, VisumPtTimeProfile> ptTimeProfiles;
  private final Map<Integer, List<VisumPtVehicleJourneySection>> sectionMapping;

  public VisumPtVehicleJourneyReader(
      NetfileLanguage language, Map<String, VisumPtLineRoute> ptLineRoutes,
      Map<String, VisumPtTimeProfile> ptTimeProfiles,
      Map<Integer, List<VisumPtVehicleJourneySection>> sections) {
    super(language);
    this.ptLineRoutes = ptLineRoutes;
    this.ptTimeProfiles = ptTimeProfiles;
    this.sectionMapping = sections;
  }

  public List<VisumPtVehicleJourney> readJourneys(Stream<Row> content) {
    return content.map(this::createJourney).collect(toList());
  }

  private VisumPtVehicleJourney createJourney(Row row) {
    String lineId = lineRouteIdOf(row);
    String profileName = profileNameOf(row);
    String profileId = lineId + ";" + profileName;

    VisumPtLineRoute route = ptLineRoutes.get(lineId);
    VisumPtTimeProfile profile = ptTimeProfiles.get(profileId);

    int number = numberOf(row);
    String name = nameOf(row);
    int departure = departureOf(row);
    int fromIndex = fromTimeProfileElementIndexOf(row);
    int toIndex = toTimeProfileElementIndexOf(row);
    List<VisumPtVehicleJourneySection> sections = this.sectionMapping.get(number);
    return new VisumPtVehicleJourney(number, name, departure, route, profile, fromIndex, toIndex,
        sections);
  }

}
