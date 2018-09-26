package edu.kit.ifv.mobitopp.data.local;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PaneldataReader;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class LocalPanelDataRepository implements PanelDataRepository {

	private final InMemoryHouseholds households;
	private final InMemoryPersons persons;

	LocalPanelDataRepository(InMemoryHouseholds households, InMemoryPersons persons) {
		super();
		this.households = households;
		this.persons = persons;
	}

	public static PanelDataRepository loadFrom(File file) {
		PaneldataReader reader = parse(file);
		InMemoryHouseholds households = createHouseholdsFrom(reader);
		InMemoryPersons persons = createPersonsFrom(reader);
		return new LocalPanelDataRepository(households, persons);
	}

	private static PaneldataReader parse(File file) {
		return new PaneldataReader(file);
	}

	private static InMemoryHouseholds createHouseholdsFrom(PaneldataReader reader) {
		return InMemoryHouseholds.createFrom(reader.readHouseholds());
	}

	private static InMemoryPersons createPersonsFrom(PaneldataReader reader) {
		return InMemoryPersons.createFrom(reader.readPersons());
	}

	@Override
	public List<PersonOfPanelData> getPersonsOfHousehold(HouseholdOfPanelDataId id) {
		return persons.getPersonsOfHousehold(id);
	}

	@Override
	public List<HouseholdOfPanelDataId> getHouseholdIds(int domCodeType) {
		return households.getHouseholdIds(domCodeType);
	}

	@Override
	public HouseholdOfPanelData getHousehold(HouseholdOfPanelDataId id) {
		return households.load(id);
	}

	@Override
	public List<HouseholdOfPanelData> getHouseholds() {
		return households.households().collect(toList());
	}

}
