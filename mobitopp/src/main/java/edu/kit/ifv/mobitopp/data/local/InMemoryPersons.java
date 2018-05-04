package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.CollectionsHelper.mergeLists;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class InMemoryPersons {

	private final Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> persons;

	private InMemoryPersons(LinkedHashMap<HouseholdOfPanelDataId, List<PersonOfPanelData>> persons) {
		super();
		this.persons = persons;
	}

	public static InMemoryPersons createFrom(List<PersonOfPanelData> persons) {
		return new InMemoryPersons(withAssigned(persons));
	}

	private static LinkedHashMap<HouseholdOfPanelDataId, List<PersonOfPanelData>> withAssigned(
			List<PersonOfPanelData> persons) {
		LinkedHashMap<HouseholdOfPanelDataId, List<PersonOfPanelData>> assigned = new LinkedHashMap<>();
		for (PersonOfPanelData person : persons) {
			HouseholdOfPanelDataId householdId = person.getId().getHouseholdId();
			assigned.computeIfAbsent(householdId, k -> new ArrayList<>());
			assigned.merge(householdId, asList(person), mergeLists());
		}
		return assigned;
	}

	public List<PersonOfPanelData> getPersonsOfHousehold(HouseholdOfPanelDataId id) {
		List<PersonOfPanelData> persons = this.persons.get(id);
		Collections.sort(persons);
		return persons;
	}

}
