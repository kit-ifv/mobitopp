package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.groupingBy;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;

public class VisumPtVehicleJourneySectionReader extends VisumBaseReader {

	private static final Map<StandardAttributes, DayOfWeek> validDayMapping = Map
		.of(StandardAttributes.validMonday, DayOfWeek.MONDAY, //
			StandardAttributes.validTuesday, DayOfWeek.TUESDAY, //
			StandardAttributes.validWednesday, DayOfWeek.WEDNESDAY, //
			StandardAttributes.validThursday, DayOfWeek.THURSDAY, //
			StandardAttributes.validFriday, DayOfWeek.FRIDAY, //
			StandardAttributes.validSaturday, DayOfWeek.SATURDAY, //
			StandardAttributes.validSunday, DayOfWeek.SUNDAY);
	private final Map<Integer, VisumVehicleCombination> vehicleCombinations;

	public VisumPtVehicleJourneySectionReader(NetfileLanguage language,
		Map<Integer, VisumVehicleCombination> vehicleCombinations) {
		super(language);
		this.vehicleCombinations = vehicleCombinations;
	}

	public Map<Integer, List<VisumPtVehicleJourneySection>> readSections(Stream<Row> content) {
		return content.map(this::createSection).collect(groupingBy(s -> s.journeyId));
	}

	private VisumPtVehicleJourneySection createSection(Row row) {
		int number = numberOf(row);
		int journeyId = row.valueAsInteger(attribute(StandardAttributes.vehicleJourneyNumber));
		int fromIndex = fromTimeProfileElementIndexOf(row);
		int toIndex = toTimeProfileElementIndexOf(row);
		int day = row.valueAsInteger(attribute(StandardAttributes.validDayNumber));
		int vehicleCombinationId = vehicleCombinationOf(row);
		VisumVehicleCombination vehicleCombination = vehicleCombinations.get(vehicleCombinationId);
		EnumSet<DayOfWeek> validDays = validDaysOf(row);
		return new VisumPtVehicleJourneySection(number, journeyId, fromIndex, toIndex, day,
			validDays, vehicleCombination);
	}

	private EnumSet<DayOfWeek> validDaysOf(Row row) {
		EnumSet<DayOfWeek> validDays = EnumSet.noneOf(DayOfWeek.class);
		for (Entry<StandardAttributes, DayOfWeek> entry : validDayMapping.entrySet()) {
			boolean isValid = row.valueAsBoolean(attribute(entry.getKey()));
			if (isValid) {
				validDays.add(entry.getValue());
			}
		}
		return validDays;
	}

}
