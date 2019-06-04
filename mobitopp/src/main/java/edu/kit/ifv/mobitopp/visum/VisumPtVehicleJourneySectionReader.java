package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    int number = row.valueAsInteger(number());
    int journeyId = row.valueAsInteger(attribute(StandardAttributes.vehicleJourneyNumber));
    int fromIndex = row.valueAsInteger(fromTimeProfileElementIndex());
    int toIndex = row.valueAsInteger(toTimeProfileElementIndex());
    int day = row.valueAsInteger(attribute(StandardAttributes.vehicleDayNumber));
    int vehicleCombinationId = vehicleCombination(row);
    VisumVehicleCombination vehicleCombination = vehicleCombinations.get(vehicleCombinationId);
    return new VisumPtVehicleJourneySection(number, journeyId, fromIndex, toIndex, day,
        vehicleCombination);
  }

  private Integer vehicleCombination(Row row) {
    String combination = row.get(vehicleCombinationNumber());
    if (combination.isEmpty()) {
      return 0;
    }
    return Integer.valueOf(combination);
  }
}
