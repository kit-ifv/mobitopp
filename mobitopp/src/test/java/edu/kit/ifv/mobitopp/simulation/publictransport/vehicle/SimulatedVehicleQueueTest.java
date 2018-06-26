package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulatedVehicleQueueTest {

	private ModifiableJourney firstJourney;
	private ModifiableJourney secondJourney;
	private Vehicle firstVehicle;
	private Vehicle secondVehicle;
	private Connections connectionsFirstVehicle;
	private Connections connectionsSecondVehicle;
	private Time firstConnectionDeparture;

	@Before
	public void initialise() {
		firstJourney = mock(ModifiableJourney.class);
		secondJourney = mock(ModifiableJourney.class);
		initialiseJourneys();
		VehicleFactory factory = new DefaultVehicleFactory();
		firstVehicle = factory.createFrom(firstJourney);
		secondVehicle = factory.createFrom(secondJourney);
	}

	private void initialiseJourneys() {
		connectionsFirstVehicle = new Connections();
		Connection firstConnection = Data.fromSomeToAnother();
		firstConnectionDeparture = firstConnection.departure();
		connectionsFirstVehicle.add(firstConnection);
		when(firstJourney.id()).thenReturn(1);
		when(firstJourney.connections()).thenReturn(connectionsFirstVehicle);
		connectionsSecondVehicle = new Connections();
		connectionsSecondVehicle.add(Data.fromAnotherToOther());
		when(secondJourney.id()).thenReturn(2);
		when(secondJourney.connections()).thenReturn(connectionsSecondVehicle);
	}

	@Test
	public void noVehiclesAvailable() {
		assertFalse(emptyQueue().hasNextUntil(firstConnectionDeparture));
	}

	@Test
	public void providesOnlyVehiclesBeforeTime() {
		Connection secondConnection = Data.fromAnotherToOther();
		Time departure = secondConnection.departure();

		VehicleQueue queue = emptyQueue();
		queue.add(firstConnectionDeparture, firstVehicle);
		queue.hasNextUntil(firstConnectionDeparture);
		queue.next();
		queue.add(departure, secondVehicle);

		assertFalse(queue.hasNextUntil(firstConnectionDeparture));
		assertTrue(queue.hasNextUntil(departure));
		queue.next();
		assertFalse(queue.hasNextUntil(departure));
	}

	@Test
	public void processedEachEventOnce() {
		VehicleQueue queue = emptyQueue();
		queue.add(firstConnectionDeparture, firstVehicle);

		assertTrue(queue.hasNextUntil(firstConnectionDeparture));
		queue.next();
		assertFalse(queue.hasNextUntil(firstConnectionDeparture));
	}

	private VehicleQueue emptyQueue() {
		return new SimulatedVehicleQueue();
	}

}
