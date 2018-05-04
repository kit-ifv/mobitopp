package edu.kit.ifv.mobitopp.simulation.publictransport;

import static edu.kit.ifv.mobitopp.publictransport.model.FootJourney.footJourney;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class FootVehicleTest {

	private FootVehicle vehicle;
	
	@Before
	public void initialise() throws Exception {
		vehicle = new FootVehicle();
	}

	@Test
	public void alwaysHasFreePlaces() throws Exception {
		assertTrue(vehicle.hasFreePlace());
	}
	
	@Test
	public void matchesToFootJourney() throws Exception {
		assertThat(vehicle.journeyId(), is(equalTo(footJourney.id())));
	}
}
