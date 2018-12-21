package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;

public class HouseholdForDemandTest {

	private int oid;
	private HouseholdId id;
	private int nominalSize;
	private int domCode;
	private Zone homeZone;
	private Location homeLocation;
	private int numberOfNotSimulatedChildren;
	private int totalNumberOfCars;
	private int income;
	private boolean canChargePrivately;

	@Before
	public void initialise() {
		oid = 1;
		short year = 2000;
		id = new HouseholdId(oid, year, 2);
		nominalSize = 3;
		domCode = 4;
		homeZone = mock(Zone.class);
		homeLocation = mock(Location.class);
		numberOfNotSimulatedChildren = 5;
		totalNumberOfCars = 6;
		income = 7;
		canChargePrivately = true;
	}

	@Test
	public void provideAttributes() {
		HouseholdForDemand household = new HouseholdForDemand(id, nominalSize, domCode, homeZone,
				homeLocation, numberOfNotSimulatedChildren, totalNumberOfCars, income, canChargePrivately);

		HouseholdAttributes attributes = household.attributes();
		
		assertThat(attributes, is(equalTo(householdAttributes())));
	}

	private HouseholdAttributes householdAttributes() {
		return new HouseholdAttributes(oid, id, nominalSize, domCode, homeZone, homeLocation,
				numberOfNotSimulatedChildren, totalNumberOfCars, income, canChargePrivately);
	}
}
