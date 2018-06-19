package edu.kit.ifv.mobitopp.simulation.publictransport;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;

public class PassengerCompartmentTest {

	private static final int capacity = 2;
	private Passenger passenger;
	private PassengerCompartment space;

	@Before
	public void initialise() {
		passenger = newPassengerWithId(0);
		Stream<Stop> stops = Stream.of(someStop(), anotherStop());
		space = PassengerCompartment.forAll(stops, capacity);
	}

	private Passenger newPassengerWithId(int id) {
		Passenger passenger = mock(Passenger.class);
		when(passenger.getOid()).thenReturn(id);
		return passenger;
	}

	@Test
	public void notifyBoardedPassengers() {
		space.board(passenger, someStop());
		
		List<Passenger> passengersAtStop = new ArrayList<>();
		space.forEachAt(someStop(), passengersAtStop::add);
		
		assertThat(passengersAtStop, contains(passenger));
		assertThat(space.count(), is(equalTo(1)));
	}
	
	@Test
	public void doesNotNotifyBoardedPassengersExitingAtOtherStops() {
		space.board(passenger, anotherStop());
		
		List<Passenger> passengersAtStop = new ArrayList<>();
		space.forEachAt(someStop(), passengersAtStop::add);
		
		assertThat(passengersAtStop, is(empty()));
		assertThat(space.count(), is(equalTo(1)));
	}
	
	@Test
	public void doesNotNotifyGetOffPassengers() {
		space.board(passenger, someStop());
		space.getOff(passenger, someStop());
		
		List<Passenger> passengersAtStop = new ArrayList<>();
		space.forEachAt(someStop(), passengersAtStop::add);
		
		assertThat(passengersAtStop, is(empty()));
		assertThat(space.count(), is(equalTo(0)));
	}
	
	@Test
	public void doesNotGetOffPassengersAtOtherStop() {
		space.board(passenger, someStop());
		space.getOff(passenger, anotherStop());
		
		List<Passenger> passengersAtStop = new ArrayList<>();
		space.forEachAt(someStop(), passengersAtStop::add);
		
		assertThat(passengersAtStop, contains(passenger));
		assertThat(space.count(), is(equalTo(1)));
	}
	
	@Test
	public void fixConcurrentModificationExceptionWhileGettingOffBecauseOfNotification() {
		space.board(passenger, someStop());
		space.board(newPassengerWithId(1), someStop());
		
		Consumer<Passenger> getOff = passenger -> space.getOff(passenger, someStop());
		space.forEachAt(someStop(), getOff);
		
		assertThat(space.count(), is(equalTo(0)));
	}
	
	@Test
	public void isEmptyWhenNobodyBoarded() {
		assertThat(space.count(), is(equalTo(0)));
	}
	
	@Test
	public void isFull() {
		space.board(passenger, someStop());
		space.board(newPassengerWithId(1), anotherStop());
		
		assertFalse(space.hasFreePlace());
	}
	
	@Test
	public void hasFreePlace() {
		space.board(passenger, someStop());
		
		assertTrue(space.hasFreePlace());
	}
}
