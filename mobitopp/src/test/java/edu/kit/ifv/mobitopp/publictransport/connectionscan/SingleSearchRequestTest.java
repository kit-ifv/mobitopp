package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fourMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.threeMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.FootJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SingleSearchRequestTest {

	private static final int maximumNumberOfStops = 4;
	private static final RelativeTime defaultTransferTime = RelativeTime.of(0, MINUTES);

	private Stop stop1;
	private Stop stop2;
	private Stop stop3;
	private Stop otherStop;

	@Before
	public void initialise() throws Exception {
		stop1 = stop().withId(0).withName("stop 1").build();
		stop2 = stop().withId(1).withName("stop 2").build();
		stop3 = stop().withId(2).withName("stop 3").build();
		otherStop = stop().withId(2).withName("other stop").build();
	}

	@Test
	public void whenOneConnectionHasBeenUpdated() throws Exception {
		Connection connection = someConnection();

		PreparedSearchRequest searchRequest = newScannedArrival(stop1, stop2, someTime());

		searchRequest.updateArrival(connection);

		Optional<PublicTransportRoute> tour = searchRequest.createRoute();

		PublicTransportRoute expectedTour = new ScannedRoute(stop1, stop2, someTime(), oneMinuteLater(),
				asList(connection));
		assertThat(tour, isPresent());
		assertThat(tour, hasValue(expectedTour));
	}

	private Connection someConnection() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	@Test
	public void whenSeveralConnectionsHaveBeenUpdated() throws Exception {
		Connection connection1 = longConnection();
		Connection connection2 = shortConnection();

		PreparedSearchRequest searchRequest = newScannedArrival(stop1, stop2, someTime());

		searchRequest.updateArrival(connection1);
		searchRequest.updateArrival(connection2);

		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(stop1, stop2, someTime(),
				threeMinutesLater(), asList(connection1));
		assertThat(route, isPresent());
		assertThat(route, hasValue(expectedRoute));
	}

	private Connection shortConnection() {
		return connection()
				.startsAt(stop1)
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private Connection longConnection() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	@Test
	public void whenConnectionOfTheSameJourneyHasBeenTakenAlready() throws Exception {
		RelativeTime changeTime = RelativeTime.of(1, MINUTES);
		Journey sameJourney = journey().build();
		Connection connection1 = connection()
				.startsAt(stop1)
				.endsAt(stopWithChangeTime(changeTime))
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(sameJourney)
				.build();
		Connection connection2 = connection()
				.startsAt(stopWithChangeTime(changeTime))
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(sameJourney)
				.build();

		PreparedSearchRequest searchRequest = newScannedArrival(stop1, otherStop(), someTime());

		searchRequest.updateArrival(connection1);
		searchRequest.updateArrival(connection2);

		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		assertThat(route, isPresent());
		PublicTransportRoute expectedRoute = new ScannedRoute(stop1, otherStop(), someTime(),
				twoMinutesLater(), asList(connection1, connection2));
		assertThat(route, hasValue(expectedRoute));
	}

	@Test
	public void whenConnectionStartsAfterLatestDepartureAtTheEndStop() throws Exception {
		Stop start = stop1;
		Stop end = stop2;
		Time time = someTime();
		PreparedSearchRequest searchRequest = newScannedArrival(start, end, time);
		
		assertFalse(searchRequest.departsAfterArrivalAtEnd(reachableConnection()));
		assertFalse(searchRequest.departsAfterArrivalAtEnd(tooLateConnection()));
		
		searchRequest.updateArrival(someConnection());
		
		assertFalse(searchRequest.departsAfterArrivalAtEnd(reachableConnection()));
		assertTrue(searchRequest.departsAfterArrivalAtEnd(tooLateConnection()));
	}
	
	private Connection reachableConnection() {
		return connection().startsAt(stop2).endsAt(stop3).departsAndArrivesAt(oneMinuteLater()).build();
	}

	private Connection tooLateConnection() {
		return connection().startsAt(stop2).endsAt(stop3).departsAndArrivesAt(twoMinutesLater()).build();
	}

	@Test
	public void whenConnectionIsNotReachable() throws Exception {
		PreparedSearchRequest searchRequest = newScannedArrival(stop1, otherStop(), someTime());

		searchRequest.updateArrival(someConnection());
		searchRequest.updateArrival(notReachable());
		searchRequest.updateArrival(reachable());

		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(stop1, otherStop(), someTime(),
				twoMinutesLater(), asList(someConnection(), reachable()));
		assertThat(route, isPresent());
		assertThat(route, hasValue(expectedRoute));
	}

	private Connection notReachable() {
		return connection()
				.startsAt(stop2)
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	private Connection reachable() {
		return connection()
				.startsAt(stop2)
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	@Test
	public void whenEndStopsAreNeighbours() throws Exception {
		stop2.addNeighbour(stop3, defaultTransferTime);

		Connection viaStop2 = someConnection();
		Connection direct = direct();

		PreparedSearchRequest searchRequest = newScannedArrival(stop1, stop3, someTime());

		searchRequest.updateArrival(viaStop2);
		searchRequest.updateArrival(direct);

		Optional<PublicTransportRoute> routeToNeighbour = searchRequest.createRoute();

		PublicTransportRoute expectedRouteToNeighbour = new ScannedRoute(stop1, stop3, someTime(),
				oneMinuteLater(), asList(viaStop2, laterByFootFrom2To3()));
		assertThat(routeToNeighbour, isPresent());
		assertThat(routeToNeighbour, hasValue(expectedRouteToNeighbour));
	}

	private Connection laterByFootFrom2To3() {
		return connection()
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(oneMinuteLater())
				.arrivesAt(oneMinuteLater())
				.partOf(FootJourney.footJourney)
				.build();
	}

	private Connection direct() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop3)
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	@Test
	public void whenStopsAreNeighboursButStopHasEarlierArrivalTime()
			throws Exception {
		stop3.addNeighbour(stop2, defaultTransferTime);

		Connection viaStop3 = viaStop3();
		Connection viaStop2 = viaStop2();

		PreparedSearchRequest searchRequest = newScannedArrival(stop1, stop2, someTime());

		searchRequest.updateArrival(viaStop3);
		searchRequest.updateArrival(viaStop2);

		Optional<PublicTransportRoute> routeToNeighbour = searchRequest.createRoute();

		PublicTransportRoute expectedRouteToNeighbour = new ScannedRoute(stop1, stop2, someTime(),
				oneMinuteLater(), asList(viaStop3, laterByFootFrom3To2()));
		assertThat(routeToNeighbour, isPresent());
		assertThat(routeToNeighbour, hasValue(expectedRouteToNeighbour));
	}

	private Connection laterByFootFrom3To2() {
		Connection byFootFromStop3ToStop2 = connection()
				.startsAt(stop3)
				.endsAt(stop2)
				.departsAt(oneMinuteLater())
				.arrivesAt(oneMinuteLater())
				.partOf(FootJourney.footJourney)
				.build();
		return byFootFromStop3ToStop2;
	}

	private Connection viaStop2() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private Connection viaStop3() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop3)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	@Test
	public void doesNotUpdateTimeAndConnectionWhenStopIsReachableViaTransferWalkWhichArrivesLaterThanAnAlreadySetConnection()
			throws Exception {
		stop2.addNeighbour(stop3, RelativeTime.of(1, MINUTES));

		Connection directToStop3 = viaStop3();
		Connection directToStop2 = someConnection();

		PreparedSearchRequest searchRequest = newScannedArrival(stop1, stop3, someTime());

		searchRequest.updateArrival(directToStop3);
		searchRequest.updateArrival(directToStop2);

		Optional<PublicTransportRoute> directRoute = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(stop1, stop3, someTime(),
				oneMinuteLater(), asList(directToStop3));
		assertThat(directRoute, isPresent());
		assertThat(directRoute, hasValue(expectedRoute));
	}

	@Test
	public void whenStartStopsAreInOneNeighbourhood() throws Exception {
		stop2.addNeighbour(stop3, defaultTransferTime);

		Connection viaStop3 = from3To1();
		Connection direct = from2To1();
		Connection transferToStop3 = byFootFrom2To3();

		PreparedSearchRequest searchRequest = newScannedArrival(stop2, stop1, someTime());

		searchRequest.updateArrival(viaStop3);
		searchRequest.updateArrival(direct);

		Optional<PublicTransportRoute> routeViaNeighbour = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(stop2, stop1, someTime(),
				oneMinuteLater(), asList(transferToStop3, viaStop3));
		assertThat(routeViaNeighbour, isPresent());
		assertThat(routeViaNeighbour, hasValue(expectedRoute));
	}
	
	@Test
	public void whenEndIsNeighbourOfStart() {
		stop2.addNeighbour(stop3, defaultTransferTime);

		Connection viaStop3 = from3To1();
		Connection direct = from2To1();
		Connection transferToStop3 = byFootFrom2To3();

		PreparedSearchRequest searchRequest = newScannedArrival(stop2, stop3, someTime());

		searchRequest.updateArrival(viaStop3);
		searchRequest.updateArrival(direct);

		Optional<PublicTransportRoute> routeToNeighbour = searchRequest.createRoute();

		PublicTransportRoute expectedRouteToNeighbour = new ScannedRoute(stop2, stop3, someTime(),
				someTime(), asList(transferToStop3));
		assertThat(routeToNeighbour, isPresent());
		assertThat(routeToNeighbour, hasValue(expectedRouteToNeighbour));
	}

	private Connection byFootFrom2To3() {
		Connection transferToStop2 = connection()
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(someTime())
				.arrivesAt(someTime())
				.partOf(FootJourney.footJourney)
				.build();
		return transferToStop2;
	}

	private Connection from2To1() {
		return connection()
				.startsAt(stop2)
				.endsAt(stop1)
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private Connection from3To1() {
		return connection()
				.startsAt(stop3)
				.endsAt(stop1)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	@Test
	public void whenStopIsNotReachable() throws Exception {
		Stop unreachable = stop().withId(2).withName("unreachable").build();
		Connection connection = someConnection();
		PreparedSearchRequest searchRequest = newScannedArrival(stop1, unreachable, someTime());

		searchRequest.updateArrival(connection);

		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		assertThat(route, isEmpty());
	}

	@Test
	public void usesWalkTimeDuringInitialisation() throws Exception {
		Stop start = stop2;
		Stop neighbouringStop = stop3;
		start.addNeighbour(neighbouringStop, RelativeTime.of(1, MINUTES));

		Connection transferToStop2 = connection()
				.startsAt(start)
				.endsAt(neighbouringStop)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(FootJourney.footJourney)
				.build();

		PreparedSearchRequest searchRequest = newScannedArrival(start, neighbouringStop, someTime());
		Optional<PublicTransportRoute> routeToNeighbour = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(start, neighbouringStop, someTime(),
				oneMinuteLater(), asList(transferToStop2));

		assertThat(routeToNeighbour, isPresent());
		assertThat(routeToNeighbour, hasValue(expectedRoute));
	}

	@Test
	public void doesNotUseMinimumChangeTimeToReachStartStop() throws Exception {
		RelativeTime changeTime = RelativeTime.of(1, MINUTES);
		Stop start = stopWithChangeTime(changeTime);
		Stop end = stop2;
		Journey someJourney = journey().build();
		Connection connection = connection()
				.startsAt(start)
				.endsAt(end)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney)
				.build();

		PreparedSearchRequest searchRequest = newScannedArrival(start, end, someTime());

		searchRequest.updateArrival(connection);

		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(start, end, someTime(), oneMinuteLater(),
				asList(connection));
		assertThat(route, hasValue(expectedRoute));
	}

	@Test
	public void doesNotUseMinimumChangeTimeToReachEndStop() throws Exception {
		RelativeTime changeTime = RelativeTime.of(1, MINUTES);
		Stop start = stop1;
		Stop end = stopWithChangeTime(changeTime);
		Journey someJourney = journey().build();
		Connection connection = connection()
				.startsAt(start)
				.endsAt(end)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney)
				.build();

		PreparedSearchRequest searchRequest = newScannedArrival(start, end, someTime());

		searchRequest.updateArrival(connection);

		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(start, end, someTime(), oneMinuteLater(),
				asList(connection));
		assertThat(route, hasValue(expectedRoute));
	}

	@Test
	public void usesMinimumChangeTimeOnlyForReachabilityCheckNotToCompareArrivalTimes()
			throws Exception {
		RelativeTime changeTime = RelativeTime.of(2, MINUTES);
		PreparedSearchRequest searchRequest = newScannedArrival(stop1, otherStop(), someTime());

		searchRequest.updateArrival(someConnection());
		searchRequest.updateArrival(toIntermediateStop(changeTime));
		searchRequest.updateArrival(laterToIntermediateStop(changeTime));
		searchRequest.updateArrival(toEndStop(changeTime));
		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		PublicTransportRoute expectedRoute = new ScannedRoute(stop1, otherStop(), someTime(),
				fourMinutesLater(),
				asList(someConnection(), toIntermediateStop(changeTime), toEndStop(changeTime)));
		assertThat(route, hasValue(expectedRoute));
	}

	private Connection toEndStop(RelativeTime changeTime) {
		return connection()
				.startsAt(stopWithChangeTime(changeTime))
				.endsAt(otherStop())
				.departsAt(fourMinutesLater())
				.arrivesAt(fourMinutesLater())
				.build();
	}

	private Connection laterToIntermediateStop(RelativeTime changeTime) {
		return connection()
				.startsAt(stop2)
				.endsAt(stopWithChangeTime(changeTime))
				.departsAt(twoMinutesLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	private Connection toIntermediateStop(RelativeTime changeTime) {
		return connection()
				.startsAt(stop2)
				.endsAt(stopWithChangeTime(changeTime))
				.departsAt(oneMinuteLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	private Stop otherStop() {
		return otherStop;
	}

	private Stop stopWithChangeTime(RelativeTime changeTime) {
		return stop()
				.withId(3)
				.withName("containing change time")
				.minimumChangeTime(changeTime)
				.build();
	}

	@Test
	public void doesNotCreateRouteWhenNoConnectionsHaveBeenProcessed() throws Exception {
		PreparedSearchRequest searchRequest = newScannedArrival(stop1, stop2, someTime());
		
		Optional<PublicTransportRoute> route = searchRequest.createRoute();

		assertThat(route, isEmpty());
	}

	private PreparedSearchRequest newScannedArrival(Stop start, Stop end, Time time) {
		return SingleSearchRequest.from(start, end, time, maximumNumberOfStops);
	}
	
}
