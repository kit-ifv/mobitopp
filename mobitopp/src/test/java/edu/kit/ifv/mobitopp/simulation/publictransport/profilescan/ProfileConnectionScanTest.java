package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromAnotherToOther;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromOtherToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromSomeToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.laterFromSomeToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.time.RelativeTime.of;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.ScannedRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.StopPaths;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class ProfileConnectionScanTest {

	private Store store;
	private Store mockedStore;
	private Profile profile;
	private Profile earlierPart;
	private Profile laterPart;
	private ArrivalTimeFunction timeFunction;

	@Before
	public void initialise() throws Exception {
		store = new DummyStore();
		mockedStore = mock(Store.class);
		profile = mock(Profile.class);
		earlierPart = mock(Profile.class);
		laterPart = mock(Profile.class);
		timeFunction = mock(ArrivalTimeFunction.class);
	}

	@Test
	public void findsArrivalTimeInStore() throws Exception {
		when(mockedStore.profileTo(anotherStop(), someTime())).thenReturn(profile);
		when(profile.from(someStop())).thenReturn(timeFunction);

		ProfileConnectionScan scan = new ProfileConnectionScan(mockedStore);

		scan.find(someStop(), anotherStop(), someTime());

		verify(mockedStore).profileTo(anotherStop(), someTime());
		verify(profile).from(someStop());
		verify(timeFunction).arrivalFor(someTime());
	}

	@Test
	public void doesNotFindRouteForMissingProfile() throws Exception {
		when(mockedStore.profileTo(anotherStop(), someTime())).thenReturn(profile);
		when(profile.from(someStop())).thenReturn(timeFunction);
		when(timeFunction.arrivalFor(someTime())).thenReturn(empty());

		ProfileConnectionScan scan = new ProfileConnectionScan(mockedStore);

		Optional<PublicTransportRoute> route = scan.findRoute(someStop(), anotherStop(), someTime());

		assertThat(route, isEmpty());
	}

	@Test
	public void buildsUpConnectionsFromStore() throws Exception {
		Connection firstConnection = mock(Connection.class);
		Connection secondConnection = mock(Connection.class);
		Journey journey = mock(Journey.class);
		ArrivalTimeFunction secondFunction = mock(ArrivalTimeFunction.class);
		when(mockedStore.profileTo(anotherStop(), oneMinuteLater())).thenReturn(laterPart);
		when(mockedStore.profileTo(anotherStop(), someTime())).thenReturn(earlierPart);
		when(laterPart.from(yetAnotherStop())).thenReturn(secondFunction);
		when(earlierPart.from(someStop())).thenReturn(timeFunction);
		when(timeFunction.arrivalFor(someTime())).thenReturn(of(twoMinutesLater()));
		when(timeFunction.connectionFor(someTime())).thenReturn(of(firstConnection));
		when(secondFunction.arrivalFor(oneMinuteLater())).thenReturn(of(twoMinutesLater()));
		when(secondFunction.connectionFor(oneMinuteLater())).thenReturn(of(secondConnection));
		when(firstConnection.end()).thenReturn(yetAnotherStop());
		when(firstConnection.arrival()).thenReturn(oneMinuteLater());
		when(firstConnection.journey()).thenReturn(journey);
		when(secondConnection.end()).thenReturn(anotherStop());
		when(secondConnection.journey()).thenReturn(journey);

		ProfileConnectionScan scan = new ProfileConnectionScan(mockedStore);

		List<Connection> connections = scan.connections(someStop(), someTime(), anotherStop());

		assertThat(connections, contains(firstConnection, secondConnection));

		verify(mockedStore).profileTo(anotherStop(), someTime());
		verify(earlierPart).from(someStop());
		verify(timeFunction).arrivalFor(someTime());
		verify(timeFunction).connectionFor(someTime());
		verify(mockedStore).profileTo(anotherStop(), oneMinuteLater());
		verify(laterPart).from(yetAnotherStop());
		verify(secondFunction).arrivalFor(oneMinuteLater());
		verify(secondFunction).connectionFor(oneMinuteLater());
		verifyNoMoreInteractions(mockedStore);
		verifyNoMoreInteractions(laterPart);
		verifyNoMoreInteractions(earlierPart);
		verifyNoMoreInteractions(timeFunction);
		verifyNoMoreInteractions(secondFunction);
	}

	@Test
	public void findsNothingWithoutConnections() throws Exception {
		Optional<Time> time = scan(noConnections()).find(someStop(), anotherStop(), someTime());

		assertThat(time, isEmpty());
	}

	private static List<Connection> noConnections() {
		return emptyList();
	}

	@Test
	public void findsSingleStopForSingleConnection() throws Exception {
		ProfileConnectionScan scan = scan(singleConnection());

		Optional<Time> time = scan.find(someStop(), anotherStop(), someTime());

		assertThat(time, hasValue(oneMinuteLater()));
	}

	private static List<Connection> singleConnection() {
		return asList(fromSomeToAnother());
	}

	@Test
	public void findsRouteFromSingleStopForSingleConnection() throws Exception {
		ProfileConnectionScan scan = scan(singleConnection());

		Optional<PublicTransportRoute> route = scan.findRoute(someStop(), anotherStop(), someTime());

		PublicTransportRoute expected = new ScannedRoute(someStop(), anotherStop(), someTime(),
				oneMinuteLater(), singleConnection());
		assertThat(route, hasValue(expected));

	}

	@Test
	public void findsDifferentArrivalTimesForDifferentStartStops() throws Exception {
		ProfileConnectionScan scan = scan(twoConnections());

		Optional<Time> arrivalFromSomeStop = scan.find(someStop(), anotherStop(), someTime());
		Optional<Time> arrivalFromOtherStop = scan.find(otherStop(), anotherStop(), someTime());

		assertThat(arrivalFromSomeStop, hasValue(oneMinuteLater()));
		assertThat(arrivalFromOtherStop, hasValue(twoMinutesLater()));
	}

	private static List<Connection> twoConnections() {
		return asList(fromSomeToAnother(), fromOtherToAnother());
	}

	@Test
	public void findsRouteFromSeveralStopsForSeveralConnections() throws Exception {
		ProfileConnectionScan scan = scan(twoConnections());

		Optional<PublicTransportRoute> routeFromSomeStop = scan.findRoute(someStop(), anotherStop(),
				someTime());
		Optional<PublicTransportRoute> routeFromOtherStop = scan.findRoute(otherStop(), anotherStop(),
				someTime());

		PublicTransportRoute expectedFromSomeStop = new ScannedRoute(someStop(), anotherStop(),
				someTime(), oneMinuteLater(), singleConnection());
		PublicTransportRoute expectedFromOtherStop = new ScannedRoute(otherStop(), anotherStop(),
				someTime(), twoMinutesLater(), asList(fromOtherToAnother()));
		assertThat(routeFromSomeStop, hasValue(expectedFromSomeStop));
		assertThat(routeFromOtherStop, hasValue(expectedFromOtherStop));
	}

	@Test
	public void findsArrivalViaStopover() throws Exception {
		ProfileConnectionScan scan = scan(stopoverConnections());

		Optional<Time> arrival = scan.find(someStop(), otherStop(), someTime());

		assertThat(arrival, hasValue(twoMinutesLater()));
	}

	@Test
	public void findsRouteViaStopover() throws Exception {
		ProfileConnectionScan scan = scan(stopoverConnections());

		Optional<PublicTransportRoute> route = scan.findRoute(someStop(), otherStop(), someTime());

		PublicTransportRoute expected = new ScannedRoute(someStop(), otherStop(), someTime(),
				twoMinutesLater(), stopoverConnections());
		assertThat(route, hasValue(expected));
	}

	@Test
	public void findsRouteBetweenSeveralStartAndEndStops() throws Exception {
		List<Stop> targetStops = asList(anotherStop(), yetAnotherStop());
		StopPaths startStops = mock(StopPaths.class);
		StopPaths endStops = mock(StopPaths.class);
		when(startStops.stops()).thenReturn(asList(someStop(), otherStop()));
		when(endStops.stops()).thenReturn(targetStops);

		ProfileConnectionScan scan = ProfileConnectionScan.from(severalToSeveral(), targetStops, store);
		Optional<PublicTransportRoute> route = scan.findRoute(startStops, endStops, someTime());

		PublicTransportRoute expected = new ScannedRoute(someStop(), anotherStop(), someTime(),
				oneMinuteLater(), asList(fromSomeToAnother()));
		assertThat(route, hasValue(expected));
	}

	@Test
	public void findsRouteBetweenSeveralStartAndEndStopsWithDifferentRoutes() throws Exception {
		List<Stop> targetStops = asList(anotherStop(), yetAnotherStop());
		StopPaths startStops = mock(StopPaths.class);
		StopPaths endStops = mock(StopPaths.class);
		when(startStops.stops()).thenReturn(asList(someStop(), otherStop()));
		when(endStops.stops()).thenReturn(targetStops);

		ProfileConnectionScan scan = ProfileConnectionScan.from(severalToSeveralOther(), targetStops,
				store);
		Optional<PublicTransportRoute> route = scan.findRoute(startStops, endStops, someTime());

		PublicTransportRoute expected = new ScannedRoute(otherStop(), yetAnotherStop(), someTime(),
				oneMinuteLater(), asList(earlierFromOtherToYetAnother()));
		assertThat(route, hasValue(expected));
	}

	@Test
	public void findsArrivalTimeWithFootpath() throws Exception {
		Stop start = stop().withId(1).build();
		Stop footStart = stop().withId(2).build();
		Stop footEnd = stop().withId(3).build();
		Stop end = stop().withId(4).build();
		footEnd.addNeighbour(footStart, of(1, MINUTES));
		footStart.addNeighbour(footEnd, of(1, MINUTES));
		Connection toFootStart = connection()
				.startsAt(start)
				.endsAt(footStart)
				.departsAndArrivesAt(someTime())
				.build();
		Connection toEnd = connection()
				.startsAt(footEnd)
				.endsAt(end)
				.departsAndArrivesAt(twoMinutesLater())
				.build();
		List<Connection> connections = asList(toFootStart, toEnd);
		ProfileConnectionScan scan = ProfileConnectionScan.from(connections, asList(end), store);

		Optional<Time> arrival = scan.find(start, end, someTime());

		assertThat(arrival, isPresent());
		assertThat(arrival, hasValue(twoMinutesLater()));
	}

	@Test
	public void findsRouteWithFootpath() throws Exception {
		Stop start = stop().withId(1).build();
		Stop footStart = stop().withId(2).build();
		Stop footEnd = stop().withId(3).build();
		Stop end = stop().withId(4).build();
		footEnd.addNeighbour(footStart, of(2, MINUTES));
		footStart.addNeighbour(footEnd, of(2, MINUTES));
		Connection toFootStart = connection()
				.startsAt(start)
				.endsAt(footStart)
				.departsAndArrivesAt(someTime())
				.build();
		Connection tooEarlyToEnd = connection()
				.startsAt(footEnd)
				.endsAt(end)
				.departsAndArrivesAt(oneMinuteLater())
				.partOf(earlierJourney())
				.build();
		Connection toEnd = connection()
				.startsAt(footEnd)
				.endsAt(end)
				.departsAndArrivesAt(twoMinutesLater())
				.partOf(laterJourney())
				.build();
		List<Connection> connections = asList(toFootStart, tooEarlyToEnd, toEnd);
		ProfileConnectionScan scan = ProfileConnectionScan.from(connections, asList(end), store);

		Optional<PublicTransportRoute> route = scan.findRoute(start, end, someTime());

		List<Connection> connectionsIncludingWalking = asList(toFootStart,
				footpathFrom(footStart, footEnd), toEnd);
		ScannedRoute expectedRoute = new ScannedRoute(start, end, someTime(), twoMinutesLater(),
				connectionsIncludingWalking);
		assertThat(route, isPresent());
		assertThat(route, hasValue(expectedRoute));
	}

	@Test
	public void whenNeighbourIsTargetUseFootpathToNeighbour() throws Exception {
		Stop start = stop().withId(1).build();
		Stop footStart = stop().withId(2).build();
		Stop end = stop().withId(3).build();
		end.addNeighbour(footStart, of(1, MINUTES));
		footStart.addNeighbour(end, of(1, MINUTES));
		Connection toFootStart = connection()
				.startsAt(start)
				.endsAt(footStart)
				.departsAndArrivesAt(someTime())
				.build();
		List<Connection> connections = asList(toFootStart);
		ProfileConnectionScan scan = ProfileConnectionScan.from(connections, asList(end), store);

		Optional<PublicTransportRoute> route = scan.findRoute(start, end, someTime());

		List<Connection> connectionsIncludingWalking = asList(toFootStart,
				footpathFrom(footStart, end));
		ScannedRoute expectedRoute = new ScannedRoute(start, end, someTime(), oneMinuteLater(),
				connectionsIncludingWalking);
		assertThat(route, isPresent());
		assertThat(route, hasValue(expectedRoute));
	}

	private Connection footpathFrom(Stop start, Stop end) {
		Time departure = someTime();
		Optional<Time> arrival = start.arrivalAt(end, departure);
		return Connection.byFootFrom(start, end, departure, arrival.get());
	}

	@Test
	public void considerChangeTime() throws Exception {
		Journey firstJourney = journey().withId(1).build();
		Journey followingJourney = journey().withId(2).build();
		Connection first = connection().partOf(firstJourney).build();
		Connection following = connection().partOf(followingJourney).build();
		ProfileConnectionScan scan = new ProfileConnectionScan(mockedStore);

		boolean considersChangeTime = scan.considerChangeTime(first, Optional.of(following));

		assertTrue(considersChangeTime);
	}

	@Test
	public void doesNotConsiderChangeTime() throws Exception {
		Journey sameJourney = journey().withId(1).build();
		Connection first = connection().partOf(sameJourney).build();
		Connection following = connection().partOf(sameJourney).build();
		ProfileConnectionScan scan = new ProfileConnectionScan(mockedStore);

		boolean considersChangeTime = scan.considerChangeTime(first, Optional.of(following));

		assertFalse(considersChangeTime);
	}

	private List<Connection> severalToSeveralOther() {
		return asList(laterFromSomeToAnother(), earlierFromOtherToYetAnother());
	}

	private List<Connection> severalToSeveral() {
		return asList(fromSomeToAnother(), fromOtherToYetAnother());
	}

	private Connection earlierFromOtherToYetAnother() {
		return connection()
				.startsAt(otherStop())
				.endsAt(yetAnotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney())
				.build();
	}

	private Connection fromOtherToYetAnother() {
		return connection()
				.startsAt(otherStop())
				.endsAt(yetAnotherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(someJourney())
				.build();
	}

	private static Journey earlierJourney() {
		return someJourney();
	}

	private static Journey laterJourney() {
		return journey().withId(2).build();
	}

	private static Journey someJourney() {
		return journey().withId(1).build();
	}

	private List<Connection> stopoverConnections() {
		return asList(fromSomeToAnother(), fromAnotherToOther());
	}

	private ProfileConnectionScan scan(List<Connection> connections) {
		return ProfileConnectionScan.from(connections, allStops(), store);
	}

	private List<Stop> allStops() {
		return asList(someStop(), anotherStop(), otherStop());
	}
}
