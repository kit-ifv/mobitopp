package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.FootJourney.footJourney;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class BasicPublicTransportBehaviourTest {

	private static final int noPerson = 0;
	private static final int onePerson = 1;

	private PublicTransportLogger logger;
	private Journey someJourney;
	private RouteSearch routeSearch;
	private PublicTransportTrip someTrip;
	private BasicPublicTransportBehaviour journeys;
	private Vehicles vehicles;
	private Vehicle someVehicle;
	private EventQueue queue;
	private PublicTransportLeg leg;
	private Stop legStart;

	@Before
	public void initialise() throws Exception {
		logger = mock(PublicTransportLogger.class);
		someJourney = mock(Journey.class);
		routeSearch = mock(RouteSearch.class);
		someTrip = mock(PublicTransportTrip.class);
		vehicles = mock(Vehicles.class);
		queue = mock(EventQueue.class);
		someVehicle = mock(Vehicle.class);
		when(vehicles.vehicleServing(someJourney)).thenReturn(someVehicle);
		legStart = Data.someStop();
		leg = mock(PublicTransportLeg.class);
		when(leg.journey()).thenReturn(someJourney);
		when(leg.start()).thenReturn(legStart);

		journeys = new BasicPublicTransportBehaviour(routeSearch, logger, vehicles) {

			@Override
			protected boolean hasPlaceInVehicle(Vehicles vehicles, PublicTransportLeg leg) {
				return true;
			}

		};
	}

	@Test
	public void logsNothingWhenNoJourneyHasBeenAdded() throws Exception {
		when(vehicles.hasNextUntil(someDate())).thenReturn(false);
		when(vehicles.next()).thenReturn(someVehicle);

		journeys.letVehiclesArriveAt(someDate(), queue);
		journeys.letVehiclesDepartAt(someDate());

		verify(logger, never()).arrive(someDate(), someVehicle);
		verify(logger, never()).depart(someDate(), someVehicle);
	}

	@Test
	public void logsArrivalOfSingleJourney() throws Exception {
		hasSingleNextVehicle();

		journeys.letVehiclesArriveAt(earliestDate(), queue);

		verify(logger).arrive(earliestDate(), someVehicle);
		verifyZeroInteractions(logger);
	}

	@Test
	public void movesVehicleToNextStop() {
		hasSingleNextVehicle();

		journeys.letVehiclesArriveAt(earliestDate(), queue);

		verify(someVehicle).moveToNextStop();
		verify(someVehicle).notifyPassengers(queue, earliestDate());
	}

	@Test
	public void logsDepartureOfSingleJourney() throws Exception {
		hasSingleNextVehicle();
		when(someVehicle.nextDeparture()).thenReturn(Optional.of(earliestDate()));
		when(someVehicle.nextArrival()).thenReturn(Optional.of(anotherDate()));
		journeys.letVehiclesArriveAt(earliestDate(), queue);

		journeys.letVehiclesDepartAt(earliestDate());

		verify(logger).depart(earliestDate(), someVehicle);
		verify(someVehicle).nextArrival();
		verify(vehicles).add(someVehicle, anotherDate());
	}

	@Test
	public void logsDepartureOfJourneyAtCorrectTime() throws Exception {
		hasSingleNextVehicle();
		journeys.letVehiclesArriveAt(earliestDate(), queue);

		journeys.letVehiclesDepartAt(someDate());

		verify(logger, never()).depart(any(), any(Vehicle.class));
		verify(vehicles, never()).add(any(), any());
	}

	private void hasSingleNextVehicle() {
		when(vehicles.hasNextUntil(earliestDate())).thenReturn(true, false);
		when(vehicles.next()).thenReturn(someVehicle);
	}

	@Test
	public void vehicleIsAvailable() throws Exception {
		hasSingleNextVehicle();
		when(someVehicle.currentStop()).thenReturn(legStart);
		journeys.letVehiclesArriveAt(earliestDate(), queue);

		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertTrue(isVehicleAvailable);
	}

	@Test
	public void vehicleIsNotAvailable() throws Exception {
		hasSingleNextVehicle();
		when(someVehicle.currentStop()).thenReturn(Data.anotherStop());
		journeys.letVehiclesArriveAt(earliestDate(), queue);

		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertFalse(isVehicleAvailable);
	}

	@Test
	public void vehicleHasNotArrived() throws Exception {
		hasSingleNextVehicle();
		when(someVehicle.currentStop()).thenReturn(legStart);

		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertFalse(isVehicleAvailable);
	}

	@Test
	public void vehicleHasDeparted() throws Exception {
		when(someVehicle.nextDeparture()).thenReturn(Optional.of(earliestDate()));
		hasSingleNextVehicle();
		when(someVehicle.currentStop()).thenReturn(legStart);
		journeys.letVehiclesArriveAt(earliestDate(), queue);
		journeys.letVehiclesDepartAt(earliestDate());

		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertFalse(isVehicleAvailable);
	}

	@Test
	public void footVehicleIsAlwaysAvailable() throws Exception {
		PublicTransportLeg leg = mock(PublicTransportLeg.class);
		when(leg.journey()).thenReturn(footJourney);
		when(leg.departure()).thenReturn(someTime());

		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertTrue(isVehicleAvailable);
	}

	@Test
	public void boardsPassengerOnCorrectVehicleAndLetPassengerGetOffTheVehicle() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.enterWaitingArea(person, someStop());
		journeys.board(person, someDate(), somePart());
		verify(logger).board(person, someDate(), somePart());
		verify(logger).vehicleCrowded(someVehicle, somePart());
		verifyNoMoreInteractions(logger);
		verify(someVehicle).board(person, somePart().end());
		verifyZeroInteractions(someJourney);

		journeys.getOff(person, someDate(), somePart());
		verify(logger).getOff(person, someDate(), somePart());
		verifyNoMoreInteractions(logger);
		verify(someVehicle).getOff(person);
		verifyZeroInteractions(someJourney);
	}

	@Test
	public void boardsSeveralPassengersOnCorrectVehicleAndLetPassengerGetOffTheVehicle()
			throws Exception {
		SimulationPerson somePerson = mock(SimulationPerson.class);
		SimulationPerson anotherPerson = mock(SimulationPerson.class);

		journeys.getOff(somePerson, someDate(), somePart());
		verify(logger).getOff(somePerson, someDate(), somePart());
		verifyNoMoreInteractions(logger);
		verify(someVehicle).getOff(somePerson);
		verifyNoMoreInteractions(someJourney);

		journeys.getOff(anotherPerson, someDate(), somePart());
		verify(logger).getOff(anotherPerson, someDate(), somePart());
		verifyNoMoreInteractions(logger);
		verify(someVehicle).getOff(anotherPerson);
		verifyNoMoreInteractions(someJourney);

		journeys.board(somePerson, someDate(), anotherPart());
		verify(logger).board(somePerson, someDate(), anotherPart());
		verify(someVehicle).board(somePerson, anotherPart().end());
		verifyZeroInteractions(someJourney);
		verify(logger).vehicleCrowded(someVehicle, anotherPart());
		verifyNoMoreInteractions(logger);

		journeys.board(anotherPerson, someDate(), anotherPart());
		verify(logger).board(anotherPerson, someDate(), anotherPart());
		verify(logger, times(2)).vehicleCrowded(someVehicle, anotherPart());
		verifyNoMoreInteractions(logger);
		verify(someVehicle).board(anotherPerson, anotherPart().end());
		verifyZeroInteractions(someJourney);
	}

	@Test
	public void logsWaitingOfPassenger() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.wait(person, someDate(), somePart());

		verify(logger).wait(person, someDate(), somePart());
	}

	@Test
	public void logsWaitingPassengersWhenVehiclesDepartWhenPassengerStartsAndEndsWaiting()
			throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.enterWaitingArea(person, someStop());
		journeys.leaveWaitingArea(person, someStop());
		journeys.letVehiclesDepartAt(someDate());

		verify(logger).waitingAt(someStop(), someDate(), noPerson);
	}

	@Test
	public void logsWaitingPassengersWhenVehiclesDepartWhenPassengerStartsAndEndsWaitingAndAnotherPassengerIsAlreadyWaiting()
			throws Exception {
		SimulationPerson somePerson = mock(SimulationPerson.class);
		SimulationPerson anotherPerson = mock(SimulationPerson.class);

		journeys.enterWaitingArea(anotherPerson, someStop());
		journeys.enterWaitingArea(somePerson, someStop());
		journeys.leaveWaitingArea(somePerson, someStop());
		journeys.letVehiclesDepartAt(someDate());

		verify(logger).waitingAt(someStop(), someDate(), onePerson);
	}

	@Test
	public void logsEmptyStationsExactlyOnceUntilTheNextPassengerArrives() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.enterWaitingArea(person, someStop());
		journeys.letVehiclesDepartAt(someDate());
		verify(logger).waitingAt(someStop(), someDate(), onePerson);

		journeys.leaveWaitingArea(person, someStop());
		journeys.letVehiclesDepartAt(someDate());
		verify(logger).waitingAt(someStop(), someDate(), noPerson);

		journeys.letVehiclesDepartAt(someDate());
		verify(logger, never()).depart(someDate(), someVehicle);
		verifyNoMoreInteractions(logger);
	}

	@Test
	public void searchesNewTourAndLogsThatAPersonNeedsANewTour() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.searchNewTrip(person, someDate(), someTrip);

		verify(someTrip).derive(oneMinuteLater(), routeSearch);
	}

	private PublicTransportLeg somePart() {
		return new PublicTransportLeg(someStop(), anotherStop(), someJourney, someTime(),
				oneMinuteLater(), emptyList());
	}

	private PublicTransportLeg anotherPart() {
		return new PublicTransportLeg(anotherStop(), someStop(), someJourney, someTime(),
				oneMinuteLater(), emptyList());
	}

	private static Time someTime() {
		return earliestDate().plusMinutes(1);
	}

	private static Time oneMinuteLater() {
		return earliestDate().plusMinutes(2);
	}

	private static Time earliestDate() {
		return Data.someTime();
	}

	private static Time someDate() {
		return earliestDate().plusMinutes(1);
	}

	private static Time anotherDate() {
		return earliestDate().plusMinutes(2);
	}
}
