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
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class PublicTransportTripTest {

	private TripIfc mockedTrip;
	private RouteSearch routeSearch;
	private PublicTransportRoute singleJourneyRoute;
	private Optional<PublicTransportRoute> optionalRoute;
	private List<PublicTransportLeg> parts;
	private PublicTransportLeg singlePart;

	@Before
	public void initialise() throws Exception {
		mockedTrip = mock(TripIfc.class);
		routeSearch = mock(RouteSearch.class);
		singleJourneyRoute = mock(PublicTransportRoute.class);
		optionalRoute = Optional.of(singleJourneyRoute);
		singlePart = mock(PublicTransportLeg.class);
		parts = asList(singlePart);
	}

	@Test
	public void travelsOverNoPartWhenTourContainsNoConnection() throws Exception {
		PublicTransportTrip trip = PublicTransportTrip.of(mockedTrip, optionalRoute);

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
		when(singlePart.start()).thenReturn(start);
		when(singlePart.end()).thenReturn(end);
		when(routeSearch.findRoute(start, end, someTime())).thenReturn(Optional.empty());
		PublicTransportTrip trip = newTrip();

		trip.derive(someTime(), routeSearch);

		verify(routeSearch).findRoute(start, end, someTime());
	}

	private PublicTransportTrip newTrip() {
		return new PublicTransportTrip(mockedTrip, optionalRoute, parts);
	}
	

	@Test
	public void usesRouteToCalculateEndDate() throws Exception {
		TripIfc originalTrip = mock(TripIfc.class);
		PublicTransportRoute route = mock(PublicTransportRoute.class);
		when(route.arrival()).thenReturn(someTime());
		PublicTransportTrip trip = PublicTransportTrip.of(originalTrip, Optional.of(route));

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

}
