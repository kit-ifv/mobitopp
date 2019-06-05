package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;
import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumPtVehicleJourneySectionReader extends VisumBaseReader {

  private final Map<Integer, VisumVehicleCombination> vehicleCombinations;

  public VisumPtVehicleJourneySectionReader(
      NetfileLanguage language, Map<Integer, VisumVehicleCombination> vehicleCombinations) {
    super(language);
    this.vehicleCombinations = vehicleCombinations;
  }

  public Map<Integer, List<VisumPtVehicleJourneySection>> readSections(Stream<Row> content) {
    return content.map(this::createSection).collect(groupingBy(s -> s.journeyId));
  }

  VisumPtVehicleJourneySection createSection(Row row) {
    int number = numberOf(row);
    int journeyId = row.valueAsInteger(attribute(StandardAttributes.vehicleJourneyNumber));
    int fromIndex = fromTimeProfileElementIndexOf(row);
    int toIndex = toTimeProfileElementIndexOf(row);
    int day = row.valueAsInteger(attribute(StandardAttributes.vehicleDayNumber));
    int vehicleCombinationId = vehicleCombinationOf(row);
    VisumVehicleCombination vehicleCombination = vehicleCombinations.get(vehicleCombinationId);
    return new VisumPtVehicleJourneySection(number, journeyId, fromIndex, toIndex, day,
        vehicleCombination);
  }
}
