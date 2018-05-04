package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData.aDomCode;
import static edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData.anId;
import static edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData.household;
import static edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData.otherDomCode;
import static edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData.otherId;
import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class InMemoryHouseholdsTest {

	@Test
	public void getHousehold() {
		InMemoryHouseholds households = createHouseholds(household);

		HouseholdOfPanelData loaded = households.load(anId);

		assertValues(loaded, household);
	}

	@Test
	public void getIdsByDomCode() {
		HouseholdOfPanelData aHousehold = ExampleHouseholdOfPanelData.household(aDomCode, anId);
		HouseholdOfPanelData otherHousehold = ExampleHouseholdOfPanelData.household(otherDomCode, otherId);

		InMemoryHouseholds households = createHouseholds(aHousehold, otherHousehold);
		List<HouseholdOfPanelDataId> householdIds = households.getHouseholdIds(aDomCode);

		assertThat(householdIds, contains(anId));
	}

	private InMemoryHouseholds createHouseholds(HouseholdOfPanelData... households) {
		return InMemoryHouseholds.createFrom(asList(households));
	}

	private static void assertValues(HouseholdOfPanelData loaded, HouseholdOfPanelData saved) {
		assertValue(HouseholdOfPanelData::id, loaded, saved);
		assertValue(HouseholdOfPanelData::numberOfCars, loaded, saved);
		assertValue(HouseholdOfPanelData::numberOfReportingPersons, loaded, saved);
		assertValue(HouseholdOfPanelData::numberOfNotReportingChildren, loaded, saved);
		assertValue(HouseholdOfPanelData::numberOfMinors, loaded, saved);
		assertValue(HouseholdOfPanelData::areaTypeAsInt, loaded, saved);
		assertValue(HouseholdOfPanelData::areaType, loaded, saved);
		assertValue(HouseholdOfPanelData::size, loaded, saved);
		assertValue(HouseholdOfPanelData::domCode, loaded, saved);
		assertValue(HouseholdOfPanelData::income, loaded, saved);
	}
}
