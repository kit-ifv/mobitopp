package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;

public class HouseholdForDemandTest {

	private int oid;
	private HouseholdId id;
	private int nominalSize;
	private int domCode;
	private int type;
	private Zone homeZone;
	private Location homeLocation;
	private int numberOfMinors;
	private int numberOfNotSimulatedChildren;
	private int totalNumberOfCars;
	private int income;
	private int incomeClass;
	private EconomicalStatus economicalStatus;
	private boolean canChargePrivately;
	private HouseholdForDemand household;

	@BeforeEach
	public void initialise() {
		oid = 1;
		short year = 2000;
		id = new HouseholdId(oid, year, 2);
		nominalSize = 3;
		domCode = 4;
		type = 5;
		homeZone = mock(Zone.class);
		homeLocation = mock(Location.class);
		numberOfMinors = 5;
		numberOfNotSimulatedChildren = 5;
		totalNumberOfCars = 6;
		income = 7;
		incomeClass = 1;
		economicalStatus = EconomicalStatus.veryLow;
		canChargePrivately = true;
    household = new HouseholdForDemand(id, nominalSize, domCode, type, homeZone, homeLocation,
        numberOfMinors, numberOfNotSimulatedChildren, totalNumberOfCars, income, incomeClass,
        economicalStatus, canChargePrivately);
	}

	@Test
	public void provideAttributes() {
		HouseholdAttributes attributes = household.attributes();

		assertThat(attributes, is(equalTo(householdAttributes())));
	}

	@Test
	void getPersonById() throws Exception {
		Person person = mock(Person.class);
		PersonId personId = new PersonId(0, id, 1);
		when(person.getId()).thenReturn(personId);
		household.addPerson(person);

		Person getPerson = household.getPerson(personId);

		assertEquals(person, getPerson);
	}

	@Test
	void getMissingPerson() throws Exception {
		Person person = mock(Person.class);
		PersonId personId = new PersonId(0, id, 1);
		when(person.getId()).thenReturn(personId);

		assertThrows(IllegalArgumentException.class, () -> household.getPerson(personId));
	}

  private HouseholdAttributes householdAttributes() {
    return new HouseholdAttributes(oid, id, nominalSize, domCode, type, homeZone, homeLocation,
        numberOfMinors, numberOfNotSimulatedChildren, totalNumberOfCars, income, incomeClass,
        economicalStatus, canChargePrivately);
  }
}
