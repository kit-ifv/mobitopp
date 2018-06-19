package edu.kit.ifv.mobitopp.simulation.publictransport;

import static edu.kit.ifv.mobitopp.publictransport.model.FootJourney.footJourney;
import static edu.kit.ifv.mobitopp.simulation.publictransport.SimulatedVehicles.footVehicle;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulatedVehiclesTest {

	private ModifiableJourney someJourney;
	private Vehicle someVehicle;
	private ModifiableJourneys someJourneys;
	private Connections connections;
	private Time departure;

	@Before
	public void initialise() {
		someJourney = mock(ModifiableJourney.class);
		mock(ModifiableJourney.class);
		initialiseJourneys();
		someVehicle = SimulatedVehicle.from(someJourney);
		someJourneys = new ModifiableJourneys();
	}

	private void initialiseJourneys() {
		connections = new Connections();
		Connection firstConnection = Data.fromSomeToAnother();
		departure = firstConnection.departure();
		connections.add(firstConnection);
		when(someJourney.id()).thenReturn(1);
		when(someJourney.connections()).thenReturn(connections);
	}

	@Test
	public void containsSingleVehiclePerJourney() {
		someJourneys.add(someJourney);
		Vehicles vehicles = vehicles();

		Vehicle vehicle = vehicles.vehicleServing(someJourney);

		assertThat(vehicle, is(equalTo(someVehicle)));
	}

	@Test
	public void containsVehicleForFootJourneys() {
		Vehicles vehicles = vehicles();

		Vehicle vehicle = vehicles.vehicleServing(footJourney);

		assertThat(vehicle, is(footVehicle));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsForMissingVehicle() {
		vehicles().vehicleServing(someJourney);
	}

	@Test
	public void initialisesEventQueue() {
		someJourneys.add(someJourney);

		Vehicles vehicles = vehicles();
		boolean hasNextVehicle = vehicles.hasNextUntil(departure);
		Vehicle nextVehicle = vehicles.next();

		assertTrue(hasNextVehicle);
		assertThat(nextVehicle, is(someVehicle));
	}

	private Vehicles vehicles() {
		return SimulatedVehicles.from(someJourneys);
	}
}
