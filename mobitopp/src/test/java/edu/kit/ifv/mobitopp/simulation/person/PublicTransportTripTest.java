package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip.asTime;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportTripTest {

	private TripIfc mockedTrip;
	private RouteSearch routeSearch;
	private PublicTransportBehaviour publicTransportBehaviour;
	private PublicTransportRoute singleJourneyRoute;
	private Optional<PublicTransportRoute> optionalRoute;
	private List<PublicTransportLeg> parts;
	private PublicTransportLeg singlePart;
  private SimulationPerson person;

	@Before
	public void initialise() throws Exception {
		mockedTrip = mock(TripIfc.class);
		person = mock(SimulationPerson.class);
		routeSearch = mock(RouteSearch.class);
		publicTransportBehaviour = mock(PublicTransportBehaviour.class);
		singleJourneyRoute = mock(PublicTransportRoute.class);
		optionalRoute = Optional.of(singleJourneyRoute);
		singlePart = mock(PublicTransportLeg.class);
		parts = asList(singlePart);
	}

	@Test
	public void travelsOverNoPartWhenTourContainsNoConnection() throws Exception {
		PublicTransportTrip trip = PublicTransportTrip.of(mockedTrip, person, publicTransportBehaviour, optionalRoute);

		Optional<PublicTransportLeg> nextJourneyPart = trip.currentLeg();
		assertThat(nextJourneyPart, isEmpty());
	}

	@Test
	public void travelsOverOnePartWhenTourContainsConnectionsOfOnlyOneJourney() throws Exception {
		PublicTransportTrip trip = newTrip();

		Optional<PublicTransportLeg> nextJourneyPart = trip.currentLeg();
		assertThat(nextJourneyPart, isPresent());
		assertThat(nextJourneyPart, hasValue(singlePart));
	}

	@Test
	public void travelsOverNoPartWhenAllPartsHaveBeenRequested() throws Exception {
		PublicTransportTrip trip = newTrip();

		trip.nextLeg();
		Optional<PublicTransportLeg> shouldBeEmpty = trip.currentLeg();

		assertThat(shouldBeEmpty, isEmpty());
	}

	@Test
	public void searchesNewTourFromStartOfCurrentPartToEndOfTrip() throws Exception {
		Stop start = someStop();
		Stop end = anotherStop();
		Time departureOfPart = someTime().plusMinutes(1);
		Time currentTime = someTime();
		when(singlePart.start()).thenReturn(start);
		when(singlePart.end()).thenReturn(end);
		when(singlePart.departure()).thenReturn(departureOfPart);
		when(routeSearch.findRoute(start, end, currentTime)).thenReturn(Optional.empty());
		PublicTransportTrip trip = newTrip();

		trip.derive(currentTime, routeSearch);

		Time afterDepartureOfPart = departureOfPart.plusSeconds(1);
		verify(routeSearch).findRoute(start, end, afterDepartureOfPart);
	}

	private PublicTransportTrip newTrip() {
		return new PublicTransportTrip(mockedTrip, person, publicTransportBehaviour, optionalRoute, parts);
	}

	@Test
	public void usesRouteToCalculateEndDate() throws Exception {
		TripIfc originalTrip = mock(TripIfc.class);
		PublicTransportRoute route = mock(PublicTransportRoute.class);
		when(route.arrival()).thenReturn(someTime());
		PublicTransportTrip trip = PublicTransportTrip.of(originalTrip, person, publicTransportBehaviour, Optional.of(route));

		trip.calculatePlannedEndDate();

		verify(route).arrival();
	}

	@Test
	public void calculatesStatistic() {
		Journey someJourney = mock(Journey.class);
		when(mockedTrip.startDate()).thenReturn(someTime());
		PublicTransportTrip trip = newTrip();
		Events events = new Events();
		events.add(new Event(PassengerEvent.board, someTime(), someJourney));
		events.add(new Event(PassengerEvent.getOff, oneMinuteLater(), someJourney));

		FinishedTrip finished = trip.finish(oneMinuteLater(), events);

		RelativeTime plannedDuration = asTime(trip.plannedDuration());
		RelativeTime realDuration = oneMinuteLater().differenceTo(someTime());
		RelativeTime additionalDuration = realDuration.minus(plannedDuration);
		Statistic statistic = events.statistic();
		statistic.add(Element.plannedDuration, plannedDuration);
		statistic.add(Element.realDuration, realDuration);
		statistic.add(Element.additionalDuration, additionalDuration);
		assertThat(finished.statistic(), is(equalTo(statistic)));
	}
	
	@Test
  public void leavesLastStop() {
    when(mockedTrip.startDate()).thenReturn(someTime());
    Events events = new Events();
    PublicTransportTrip trip = newTrip();
    when(singlePart.end()).thenReturn(anotherStop());

    trip.finish(someTime(), events);
    
    verify(publicTransportBehaviour).leaveWaitingArea(person, singlePart.end());
  }
	
	@Test
	public void nextChangeAtLegDeparture() {
		Time departure = someTime();
		when(singlePart.departure()).thenReturn(departure);
		PublicTransportTrip trip = newTrip();
		
		Optional<Time> nextChange = trip.timeOfNextChange();
		
		assertThat(nextChange, hasValue(departure));
	}

	protected DemandSimulationEventIfc enterStartStop(
			SimulationPerson person, PublicTransportTrip trip, Time departure) {
		return edu.kit.ifv.mobitopp.simulation.events.Event.enterStartStop(person, trip, departure);
	}

}
