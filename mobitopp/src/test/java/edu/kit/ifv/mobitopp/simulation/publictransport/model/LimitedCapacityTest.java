package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;

public class LimitedCapacityTest {

	private Journey someJourney;
	private BasicPublicTransportBehaviour journeys;
	private Vehicle someVehicle;
	private RouteSearch routeSearch;
	private PublicTransportLogger logger;
	private Vehicles vehicles;

	@Before
	public void initialise() throws Exception {
		logger = mock(PublicTransportLogger.class);
		someJourney = mock(Journey.class);
		routeSearch = mock(RouteSearch.class);
		vehicles = mock(Vehicles.class);
		someVehicle = mock(Vehicle.class);
		when(vehicles.vehicleServing(someJourney)).thenReturn(someVehicle);

		journeys = new LimitedCapacity(routeSearch, logger, vehicles);
	}

	@Test
	public void hasPlaceInVehicle() throws Exception {
		vehicleHasFreePlace();
		PublicTransportLeg leg = mock(PublicTransportLeg.class);
		when(leg.journey()).thenReturn(someJourney);

		boolean hasPlaceInVehicle = journeys.hasPlaceInVehicle(leg);

		assertTrue(hasPlaceInVehicle);
		verify(someVehicle).hasFreePlace();
	}

	private void vehicleHasFreePlace() {
		when(someVehicle.hasFreePlace()).thenReturn(true);
	}

	@Test
	public void hasNoPlaceInVehicle() throws Exception {
		vehicleIsFull();
		PublicTransportLeg leg = mock(PublicTransportLeg.class);
		when(leg.journey()).thenReturn(someJourney);

		boolean hasPlaceInVehicle = journeys.hasPlaceInVehicle(leg);

		assertFalse(hasPlaceInVehicle);
		verify(someVehicle).hasFreePlace();
	}

	private void vehicleIsFull() {
		when(someVehicle.hasFreePlace()).thenReturn(false);
	}
}
