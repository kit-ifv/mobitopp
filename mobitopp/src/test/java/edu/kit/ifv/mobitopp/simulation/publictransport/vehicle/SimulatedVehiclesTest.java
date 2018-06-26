package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulatedVehiclesTest {

	private ModifiableJourney someJourney;
	private Vehicle someVehicle;
	private Time someTime;
	private SimulatedVehicles vehicles;
	private VehicleQueue queue;

	@Before
	public void initialise() {
		someJourney = mock(ModifiableJourney.class);
		someVehicle = mock(Vehicle.class);
		someTime = Data.someTime();
		Map<Journey, Vehicle> mapping = Collections.singletonMap(someJourney, someVehicle);
		queue = mock(VehicleQueue.class);
		vehicles = new SimulatedVehicles(mapping, queue);
	}

	@Test
	public void containsSingleVehiclePerJourney() {
		Vehicle vehicle = vehicles.vehicleServing(someJourney);

		assertThat(vehicle, is(equalTo(someVehicle)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsForMissingVehicle() {
		Journey missingJourney = journey().withId(2).build();
		vehicles.vehicleServing(missingJourney);
	}

	@Test
	public void initialisesEventQueue() {
		when(queue.hasNextUntil(someTime)).thenReturn(true);
		when(queue.next()).thenReturn(someVehicle);
		
		boolean hasNextVehicle = vehicles.hasNextUntil(someTime);
		Vehicle nextVehicle = vehicles.next();

		assertTrue(hasNextVehicle);
		assertThat(nextVehicle, is(someVehicle));
	}
}
