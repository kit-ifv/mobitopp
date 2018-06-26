package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulatedVehicleTest {

	private static final int smallVehicle = 1;
	private static final int bigVehicle = 2;
	private Vehicle vehicle;
	private Passenger person;
	private Journey journey;
	private Connections connections;
	private Connection firstConnection;
	private Connection secondConnection;

	@Before
	public void initialise() throws Exception {
		person = mock(Passenger.class);
		journey = mock(Journey.class);
		connections = new Connections();
		firstConnection = Data.fromSomeToAnother();
		secondConnection = Data.fromAnotherToOther();
		connections.add(firstConnection);
		connections.add(secondConnection);
		when(journey.connections()).thenReturn(connections);
		vehicle = simulatedVehicle();
		exitDepot();
	}

	private void exitDepot() {
		vehicle.moveToNextStop(firstConnection.departure());
	}

	@Test
	public void doesNotHavePassengerOnBoardWhenNoPassengerBoarded() throws Exception {
		assertThat(vehicle.passengerCount(), is(0));
	}

	@Test
	public void doesNotHavePassengerOnBoardWhenPassengerAlreadyGetOff() throws Exception {
		vehicle.board(person, firstConnection.end());
		vehicle.moveToNextStop(firstConnection.arrival());
		vehicle.getOff(person);

		assertThat(vehicle.passengerCount(), is(0));
	}

	@Test
	public void hasPassengerOnBoardWhenPassengerBoarded() throws Exception {
		vehicle.board(person, firstConnection.end());

		assertThat(vehicle.passengerCount(), is(1));
	}

	@Test
	public void isNotFullWhenVehicleIsEmpty() throws Exception {
		useSmallVehicle();
		
		Vehicle vehicle = simulatedVehicle();

		assertTrue(vehicle.hasFreePlace());
		verify(journey, atLeastOnce()).capacity();
	}

	@Test
	public void isNotFullWhenCapacityIsNotReached() throws Exception {
		useBigVehicle();
		
		Vehicle vehicle = simulatedVehicle();

		vehicle.board(person, firstConnection.end());

		assertTrue(vehicle.hasFreePlace());
		verify(journey, atLeastOnce()).capacity();
	}

	@Test
	public void isFullWhenCapacityIsReached() throws Exception {
		useSmallVehicle();

		vehicle.board(person, firstConnection.end());

		assertFalse(vehicle.hasFreePlace());
		verify(journey).capacity();
	}

	@Test
	public void calculatesFirstDeparture() {
		Time departureOfFirstConnection = firstConnection.departure();
		Time firstDeparture = vehicle.firstDeparture();

		assertThat(firstDeparture, is(equalTo(departureOfFirstConnection)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyConnectionsAreExisting() {
		when(journey.connections()).thenReturn(new Connections());
		simulatedVehicle();
	}

	@Test
	public void movesVehicle() {
		Connections connections = new Connections();
		connections.add(firstConnection);
		when(journey.connections()).thenReturn(connections);
		Vehicle vehicle = simulatedVehicle();

		Stop depot = vehicle.currentStop();
		Optional<Time> departureAtDepot = vehicle.nextDeparture();
		vehicle.moveToNextStop(firstConnection.departure());
		Stop start = vehicle.currentStop();
		Optional<Time> departureAtStart = vehicle.nextDeparture();
		vehicle.moveToNextStop(firstConnection.arrival());
		Stop end = vehicle.currentStop();
		Optional<Time> departureAtEnd = vehicle.nextDeparture();

		assertThat(depot, is(not(equalTo(start))));
		assertThat(start, is(equalTo(firstConnection.start())));
		assertThat(end, is(equalTo(firstConnection.end())));
		assertThat(departureAtDepot, hasValue(firstConnection.departure()));
		assertThat(departureAtStart, hasValue(firstConnection.departure()));
		assertThat(departureAtEnd, isEmpty());
	}

	private Vehicle simulatedVehicle() {
		VehicleFactory factory = new DefaultVehicleFactory();
		return factory.createFrom(journey);
	}
	
	@Test
	public void notifiesPassengerOnArrival() {
		Passenger anotherPerson = mock(Passenger.class);
		EventQueue queue = mock(EventQueue.class);
		Time currentDate = firstConnection.arrival();
		Stop exitStop = firstConnection.end();
		Stop laterExit = secondConnection.end();
		
		vehicle.board(person, exitStop);
		vehicle.board(anotherPerson, laterExit);
		vehicle.moveToNextStop(currentDate);
		vehicle.notifyPassengers(queue, currentDate);
		
		verify(person).arriveAtStop(queue, currentDate);
		verify(anotherPerson, never()).arriveAtStop(queue, currentDate);
	}

	private void useSmallVehicle() {
		when(journey.capacity()).thenReturn(smallVehicle);
	}

	private void useBigVehicle() {
		when(journey.capacity()).thenReturn(bigVehicle);
	}
}
