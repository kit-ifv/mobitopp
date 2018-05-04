package edu.kit.ifv.mobitopp.simulation.emobility;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.simulation.Location;

public class LimitedChargingDataForZoneTest {

	private static final DefaultPower defaultPower = DefaultPower.zero;
	private ChargingFacility facility;
	private Location location;

	@Before
	public void initialise() throws Exception {
		facility = mock(ChargingFacility.class);
		location = new Example().location();
	}

	@Test
	public void providesFreeChargingPoints() throws Exception {
		canChargeAtFacility();

		int chargingPoints = singleChargingPoint().numberOfAvailableChargingPoints();

		assertThat(chargingPoints, is(1));
	}

	@Test
	public void providesNoChargingPoints() throws Exception {
		cannotChargeAtFacility();

		int chargingPoints = singleChargingPoint().numberOfAvailableChargingPoints();

		assertThat(chargingPoints, is(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsWithoutFreeChargingPoint() throws Exception {
		singleChargingPoint().freshChargingPoint(location, defaultPower);
	}

	private void canChargeAtFacility() {
		when(facility.isFree()).thenReturn(true);
	}

	private void cannotChargeAtFacility() {
		when(facility.isFree()).thenReturn(false);
	}

	private LimitedChargingDataForZone singleChargingPoint() {
		return new LimitedChargingDataForZone(asList(facility), defaultPower);
	}

}
