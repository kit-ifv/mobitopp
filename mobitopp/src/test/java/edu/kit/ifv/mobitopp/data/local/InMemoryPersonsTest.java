package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData.anId;
import static edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData.otherId;
import static edu.kit.ifv.mobitopp.data.local.ExamplePersonOfPanelData.aPerson;
import static edu.kit.ifv.mobitopp.data.local.ExamplePersonOfPanelData.otherPerson;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class InMemoryPersonsTest {

	@Test
	public void getPersonsPerHouseholds() {
		InMemoryPersons persons = createPersons();

		assertThat(persons.getPersonsOfHousehold(anId), contains(aPerson));
		assertThat(persons.getPersonsOfHousehold(otherId), contains(otherPerson));
	}

	private InMemoryPersons createPersons() {
		return InMemoryPersons.createFrom(asList(aPerson, otherPerson));
	}
}
