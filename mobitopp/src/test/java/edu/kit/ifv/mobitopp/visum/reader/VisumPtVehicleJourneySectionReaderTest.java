package edu.kit.ifv.mobitopp.visum.reader;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumJourneySection;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumVehicleCombination;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;

public class VisumPtVehicleJourneySectionReaderTest {

	@Test
	void isValidOnAllDaysIfNoInformationIsGiven() {
		VisumVehicleCombination combination = visumVehicleCombination().build();
		Map<Integer, VisumVehicleCombination> vehicleCombinations = Map
			.of(combination.id, combination);
		VisumPtVehicleJourneySectionReader reader = new VisumPtVehicleJourneySectionReader(
			language(), vehicleCombinations);
		
		Map<Integer, List<VisumPtVehicleJourneySection>> sections = reader.readSections(baseRow());
		
		VisumPtVehicleJourneySection section = visumJourneySection().withValidDays(EnumSet.allOf(DayOfWeek.class)).build();
		assertThat(sections.values()).contains(List.of(section));
	}
	
	private Stream<Row> baseRow() {
		Row row = new Row(baseContent());
		return Stream.of(row);
	}
	
	private Map<String, String> baseContent() {
		VisumPtVehicleJourneySection section = section();
		Map<String, String> values = new LinkedHashMap<>();
		values.put(language().resolve(StandardAttributes.number), String.valueOf(section.id));
		values.put(language().resolve(StandardAttributes.vehicleJourneyNumber), String.valueOf(section.journeyId));
		values.put(language().resolve(StandardAttributes.fromTimeProfileElementIndex), String.valueOf(section.fromElementIndex));
		values.put(language().resolve(StandardAttributes.toTimeProfileElementIndex), String.valueOf(section.toElementIndex));
		values.put(language().resolve(StandardAttributes.validDayNumber), String.valueOf(section.day));
		values.put(language().resolve(StandardAttributes.vehicleCombinationNumber), String.valueOf(section.vehicle.id));
		return values;
	}
	
	@Test
	void ensureCorrectValidDays() {
		VisumVehicleCombination combination = visumVehicleCombination().build();
		Map<Integer, VisumVehicleCombination> vehicleCombinations = Map
			.of(combination.id, combination);
		VisumPtVehicleJourneySectionReader reader = new VisumPtVehicleJourneySectionReader(
			language(), vehicleCombinations);

		Map<Integer, List<VisumPtVehicleJourneySection>> sections = reader.readSections(validRow());

		assertThat(sections.values()).contains(List.of(section()));
	}
	
	
	private VisumPtVehicleJourneySection section() {
		return visumJourneySection().withValidDays(DayOfWeek.TUESDAY).build();
	}

	private Stream<Row> validRow() {
		VisumPtVehicleJourneySection section = section();
		Map<String, String> values = baseContent();
		values.put(language().resolve(StandardAttributes.validMonday), String.valueOf(section.validDays.contains(DayOfWeek.MONDAY)));
		values.put(language().resolve(StandardAttributes.validTuesday), String.valueOf(section.validDays.contains(DayOfWeek.TUESDAY)));
		values.put(language().resolve(StandardAttributes.validWednesday), String.valueOf(section.validDays.contains(DayOfWeek.WEDNESDAY)));
		values.put(language().resolve(StandardAttributes.validThursday), String.valueOf(section.validDays.contains(DayOfWeek.THURSDAY)));
		values.put(language().resolve(StandardAttributes.validFriday), String.valueOf(section.validDays.contains(DayOfWeek.FRIDAY)));
		values.put(language().resolve(StandardAttributes.validSaturday), String.valueOf(section.validDays.contains(DayOfWeek.SATURDAY)));
		values.put(language().resolve(StandardAttributes.validSunday), String.valueOf(section.validDays.contains(DayOfWeek.SUNDAY)));
		Row row = new Row(values);
		return Stream.of(row);
	}

	private NetfileLanguage language() {
		return StandardNetfileLanguages.defaultSystems().english();
	}

}
