package edu.kit.ifv.mobitopp.simulation.emobility;

import static edu.kit.ifv.mobitopp.simulation.emobility.UnlimitedChargingDataForZone.unlimitedId;
import static edu.kit.ifv.mobitopp.simulation.emobility.UnlimitedChargingDataForZone.unlimitedPoints;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type;

public class UnlimitedChargingDataForZoneTest {

	private static final DefaultPower defaultPower = DefaultPower.zero;

	private BaseChargingDataForZone data;
	private Location location;

	@Before
	public void initialise() throws Exception {
		location = new Example().location();
		data = new UnlimitedChargingDataForZone(emptyList(), defaultPower);
	}

	@Test
	public void hasUnlimitedChargingPoints() throws Exception {
		int chargingPoints = data.numberOfAvailableChargingPoints();

		assertThat(chargingPoints, is(unlimitedPoints));
	}

	@Test
	public void alwaysCreateNewChargingFacility() throws Exception {
		ChargingFacility expected = new ChargingFacility(unlimitedId, location, Type.PUBLIC,
				defaultPower.publicFacility());

		ChargingFacility newFacility = data.freshChargingPoint(location, defaultPower);

		assertThat(newFacility, is(expected));
	}
}
