package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.FootJourney.footJourney;
import static java.util.Arrays.asList;
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

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.person.VehicleLeg;
import edu.kit.ifv.mobitopp.time.Time;

public class BasicPublicTransportBehaviourTest {

	private static final int noPerson = 0;
	private static final int onePerson = 1;

	private PublicTransportResults results;
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
		results = mock(PublicTransportResults.class);
		someJourney = mock(Journey.class);
		routeSearch = mock(RouteSearch.class);
		someTrip = mock(PublicTransportTrip.class);
		vehicles = mock(Vehicles.class);
		queue = mock(EventQueue.class);
		someVehicle = mock(Vehicle.class);
		when(vehicles.vehicleServing(someJourney)).thenReturn(someVehicle);
		legStart = someStop();
		leg = newLeg();

		journeys = new BasicPublicTransportBehaviour(routeSearch, results, vehicles) {

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

		verify(results, never()).arrive(someDate(), someVehicle);
		verify(results, never()).depart(someDate(), someVehicle);
	}

	@Test
	public void logsArrivalOfSingleJourney() throws Exception {
		hasSingleNextVehicle();

		journeys.letVehiclesArriveAt(earliestDate(), queue);

		verify(results).arrive(earliestDate(), someVehicle);
		verifyZeroInteractions(results);
	}

	@Test
	public void movesVehicleToNextStop() {
		hasSingleNextVehicle();

		journeys.letVehiclesArriveAt(earliestDate(), queue);

		verify(someVehicle).moveToNextStop(earliestDate());
		verify(someVehicle).notifyPassengers(queue, earliestDate());
	}

	@Test
	public void logsDepartureOfSingleJourney() throws Exception {
		hasSingleNextVehicle();
		when(someVehicle.nextDeparture()).thenReturn(Optional.of(earliestDate()));
		when(someVehicle.nextArrival()).thenReturn(Optional.of(anotherDate()));
		journeys.letVehiclesArriveAt(earliestDate(), queue);

		journeys.letVehiclesDepartAt(earliestDate());

		verify(results).depart(earliestDate(), someVehicle);
		verify(someVehicle).nextArrival();
		verify(vehicles).add(someVehicle, anotherDate());
	}

	@Test
	public void logsDepartureOfJourneyAtCorrectTime() throws Exception {
		hasSingleNextVehicle();
		journeys.letVehiclesArriveAt(earliestDate(), queue);

		journeys.letVehiclesDepartAt(someDate());

		verify(results, never()).depart(any(), any(Vehicle.class));
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

		boolean hasVehicleDeparted = journeys.hasVehicleDeparted(leg);
		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertFalse(hasVehicleDeparted);
		assertTrue(isVehicleAvailable);
	}

	@Test
	public void vehicleIsNotAvailable() throws Exception {
		hasSingleNextVehicle();
		when(someVehicle.currentStop()).thenReturn(Data.anotherStop());
		journeys.letVehiclesArriveAt(earliestDate(), queue);

		boolean hasVehicleDeparted = journeys.hasVehicleDeparted(leg);
		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertFalse(hasVehicleDeparted);
		assertFalse(isVehicleAvailable);
	}

	@Test
	public void vehicleHasNotArrived() throws Exception {
		hasSingleNextVehicle();

		boolean hasVehicleDeparted = journeys.hasVehicleDeparted(leg);
		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertFalse(hasVehicleDeparted);
		assertFalse(isVehicleAvailable);
	}

	@Test
	public void vehicleHasDeparted() throws Exception {
		Time departure = someConnection().departure();
		when(someVehicle.nextConnection()).thenReturn(Optional.of(someConnection().id()));
		when(someVehicle.nextDeparture()).thenReturn(Optional.of(departure));
		hasSingleNextVehicle();
		journeys.letVehiclesArriveAt(departure, queue);
		journeys.letVehiclesDepartAt(departure);

		boolean hasVehicleDeparted = journeys.hasVehicleDeparted(leg);
		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertTrue(hasVehicleDeparted);
		assertFalse(isVehicleAvailable);
	}

	private PublicTransportLeg newLeg() {
		Stop start = legStart;
		Stop end = someConnection().end();
		Journey journey = someJourney;
		Time departure = someConnection().departure();
		Time arrival = someConnection().arrival();
		List<Connection> connections = someConnections();
		return new VehicleLeg(start, end, journey, departure, arrival, connections);
	}

	private List<Connection> someConnections() {
		return asList(someConnection());
	}

	private Connection someConnection() {
		return connection().startsAt(legStart).departsAndArrivesAt(earliestDate()).build();
	}

	@Test
	public void footVehicleIsAlwaysAvailable() throws Exception {
		PublicTransportLeg leg = mock(VehicleLeg.class);
		when(leg.journey()).thenReturn(footJourney);
		when(leg.departure()).thenReturn(someTime());

		boolean isVehicleAvailable = journeys.isVehicleAvailable(leg);

		assertTrue(isVehicleAvailable);
	}

	@Test
	public void boardsPassengerOnCorrectVehicleAndLetPassengerGetOffTheVehicle() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.enterWaitingArea(person, someStop());
		journeys.board(person, someDate(), somePart(), someTrip);
		verify(results).board(person, someDate(), somePart(), someTrip);
		verify(results).vehicleCrowded(someVehicle, somePart());
		verifyNoMoreInteractions(results);
		verify(someVehicle).board(person, somePart().end());
		verifyZeroInteractions(someJourney);

		journeys.getOff(person, someDate(), somePart(), someTrip);
		verify(results).getOff(person, someDate(), somePart(), someTrip);
		verifyNoMoreInteractions(results);
		verify(someVehicle).getOff(person);
		verifyZeroInteractions(someJourney);
	}

	@Test
	public void boardsSeveralPassengersOnCorrectVehicleAndLetPassengerGetOffTheVehicle()
			throws Exception {
		SimulationPerson somePerson = mock(SimulationPerson.class);
		SimulationPerson anotherPerson = mock(SimulationPerson.class);

		journeys.getOff(somePerson, someDate(), somePart(), someTrip);
		verify(results).getOff(somePerson, someDate(), somePart(), someTrip);
		verifyNoMoreInteractions(results);
		verify(someVehicle).getOff(somePerson);
		verifyNoMoreInteractions(someJourney);

		journeys.getOff(anotherPerson, someDate(), somePart(), someTrip);
		verify(results).getOff(anotherPerson, someDate(), somePart(), someTrip);
		verifyNoMoreInteractions(results);
		verify(someVehicle).getOff(anotherPerson);
		verifyNoMoreInteractions(someJourney);

		journeys.board(somePerson, someDate(), anotherPart(), someTrip);
		verify(results).board(somePerson, someDate(), anotherPart(), someTrip);
		verify(someVehicle).board(somePerson, anotherPart().end());
		verifyZeroInteractions(someJourney);
		verify(results).vehicleCrowded(someVehicle, anotherPart());
		verifyNoMoreInteractions(results);

		journeys.board(anotherPerson, someDate(), anotherPart(), someTrip);
		verify(results).board(anotherPerson, someDate(), anotherPart(), someTrip);
		verify(results, times(2)).vehicleCrowded(someVehicle, anotherPart());
		verifyNoMoreInteractions(results);
		verify(someVehicle).board(anotherPerson, anotherPart().end());
		verifyZeroInteractions(someJourney);
	}

	@Test
	public void logsWaitingOfPassenger() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.wait(person, someDate(), somePart(), someTrip);

		verify(results).wait(person, someDate(), somePart(), someTrip);
	}

	@Test
	public void logsWaitingPassengersWhenVehiclesDepartWhenPassengerStartsAndEndsWaiting()
			throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.enterWaitingArea(person, someStop());
		journeys.leaveWaitingArea(person, someStop());
		journeys.letVehiclesDepartAt(someDate());

		verify(results).waitingAt(someStop(), someDate(), noPerson);
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

		verify(results).waitingAt(someStop(), someDate(), onePerson);
	}

	@Test
	public void logsEmptyStationsExactlyOnceUntilTheNextPassengerArrives() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		journeys.enterWaitingArea(person, someStop());
		journeys.letVehiclesDepartAt(someDate());
		verify(results).waitingAt(someStop(), someDate(), onePerson);

		journeys.leaveWaitingArea(person, someStop());
		journeys.letVehiclesDepartAt(someDate());
		verify(results).waitingAt(someStop(), someDate(), noPerson);

		journeys.letVehiclesDepartAt(someDate());
		verify(results, never()).depart(someDate(), someVehicle);
		verifyNoMoreInteractions(results);
	}

	@Test
	public void searchesNewTourAndLogsThatAPersonNeedsANewTour() throws Exception {
		SimulationPerson person = mock(SimulationPerson.class);

		Time currentTime = someDate();
		journeys.searchNewTrip(person, currentTime, someTrip);

		verify(someTrip).derive(currentTime, routeSearch);
	}

	private PublicTransportLeg somePart() {
		return new VehicleLeg(someStop(), anotherStop(), someJourney, someTime(),
				oneMinuteLater(), someConnections());
	}

	private PublicTransportLeg anotherPart() {
		return new VehicleLeg(anotherStop(), someStop(), someJourney, someTime(),
				oneMinuteLater(), someConnections());
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
