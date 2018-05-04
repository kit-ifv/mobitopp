package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class WaitingAreaTest {

	private static final int noPerson = 0;
	private static final int onePerson = 1;
	private static final int twoPersons = 2;

	private WaitingArea waiting;
	private PublicTransportLogger logger;
	private SimulationPerson somePerson;
	private SimulationPerson anotherPerson;

	@Before
	public void initialise() {
		somePerson = mock(SimulationPerson.class);
		anotherPerson = mock(SimulationPerson.class);
		logger = mock(PublicTransportLogger.class);

		waiting = new WaitingArea();
	}

	@Test
	public void logsAmountOfWaitingPassengersWhenPassengerWaitAtTheSameStop() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.enterAt(someStop(), anotherPerson);
		waiting.leaveFrom(someStop(), somePerson);
		
		waiting.logOn(logger, someTime());
		
		verify(logger).waitingAt(someStop(), someTime(), onePerson);
		verifyNoMoreInteractions(logger);
	}

	@Test
	public void logsAmountOfWaitingPassengersWhenPassengerWaitAtDifferentStops() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.enterAt(anotherStop(), anotherPerson);
		waiting.leaveFrom(someStop(), somePerson);

		waiting.logOn(logger, someTime());
		
		verify(logger).waitingAt(someStop(), someTime(), noPerson);
		verify(logger).waitingAt(anotherStop(), someTime(), onePerson);
		verifyNoMoreInteractions(logger);
	}

	@Test
	public void logsNothingWhenNoPersonIsWaiting() throws Exception {
		waiting.logOn(logger, someTime());

		verifyZeroInteractions(logger);
	}

	@Test
	public void logsSingleWaitingPersonAtOneStop() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.logOn(logger, someTime());

		verify(logger).waitingAt(someStop(), someTime(), onePerson);
	}

	@Test
	public void logsSeveralWaitingPersonsAtOneStop() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.enterAt(someStop(), anotherPerson);
		waiting.logOn(logger, someTime());

		verify(logger).waitingAt(someStop(), someTime(), twoPersons);
	}

	@Test
	public void logsSeveralWaitingPersonsAtSeveralStops() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.enterAt(anotherStop(), anotherPerson);
		waiting.logOn(logger, someTime());

		verify(logger).waitingAt(someStop(), someTime(), onePerson);
		verify(logger).waitingAt(anotherStop(), someTime(), onePerson);
	}

	@Test
	public void logsOneWaitingPersonWhenPersonIsAddedSeveralTimesAtTheSameStop() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.enterAt(someStop(), somePerson);
		waiting.logOn(logger, someTime());

		verify(logger).waitingAt(someStop(), someTime(), onePerson);
	}

	@Test
	public void remainsEmptyOnClear() throws Exception {
		waiting.clearEmptyStops();
		waiting.logOn(logger, someTime());

		verifyZeroInteractions(logger);
	}

	@Test
	public void doesNotLogSingleEmptyStopAfterClear() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.leaveFrom(someStop(), somePerson);
		waiting.clearEmptyStops();
		waiting.logOn(logger, someTime());

		verifyZeroInteractions(logger);
	}

	@Test
	public void logsStopWithWaitingPassengerAfterClear() throws Exception {
		waiting.enterAt(someStop(), somePerson);
		waiting.clearEmptyStops();
		waiting.logOn(logger, someTime());

		verify(logger).waitingAt(someStop(), someTime(), onePerson);
	}
	
	@Test
	public void notifiesWaitingPassengers() {
		EventQueue queue = mock(EventQueue.class);
		Vehicle vehicle = mock(Vehicle.class);
		when(vehicle.currentStop()).thenReturn(someStop());
		
		waiting.enterAt(someStop(), somePerson);
		
		waiting.notifyWaitingPassengers(queue, vehicle, someTime());
		
		verify(somePerson).vehicleArriving(queue, vehicle, someTime());
	}

	private static Time someTime() {
		return Data.someTime();
	}

}
