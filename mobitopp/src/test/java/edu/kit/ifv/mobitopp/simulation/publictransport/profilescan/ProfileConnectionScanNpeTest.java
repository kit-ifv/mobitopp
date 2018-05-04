package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.time.RelativeTime.of;
import static edu.kit.ifv.mobitopp.util.matcher.RouteMatcher.arrivesAt;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.InMemory;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ProfileConnectionScanNpeTest {

	private static final RelativeTime changeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime rideTime = RelativeTime.of(1, MINUTES);

	private Stop end;
	private Stop start;
	private Stop stopover;
	private ModifiableJourney journey1;
	private ModifiableJourney journey2;
	private ModifiableJourney journey3;
	private Time arrivalStopoverToEndLater;
	private Time departureStopoverToEndLater;
	private Time arrivalStopoverToEnd;
	private Time departureStopoverToEnd;
	private Time arrivalStartToStopover;
	private Time departureStartToStopover;

	private List<Connection> connections;
	private Time routeDeparture;

	@Before
	public void initialise() throws Exception {
		end = end();
		start = start();
		stopover = stopover();

		journey1 = journey().withId(1).build();
		journey2 = journey().withId(2).build();
		journey3 = journey().withId(3).build();

		Time day = SimpleTime.ofSeconds(0);

		departureStartToStopover = day.plus(RelativeTime.of(0, MINUTES));
		arrivalStartToStopover = departureStartToStopover.plus(rideTime);
		departureStopoverToEnd = arrivalStartToStopover;
		arrivalStopoverToEnd = departureStopoverToEnd.plus(rideTime);
		departureStopoverToEndLater = arrivalStartToStopover.plus(changeTime);
		arrivalStopoverToEndLater = departureStopoverToEndLater.plus(rideTime);
		routeDeparture = departureStartToStopover;

		connections = asList(startToStopover(), stopoverToEnd(), stopoverToEndLater());
	}

	private Connection startToStopover() {
		Connection connection = connection()
				.withId(1)
				.startsAt(start)
				.endsAt(stopover)
				.departsAt(departureStartToStopover)
				.arrivesAt(arrivalStartToStopover)
				.partOf(journey1)
				.build();
		journey3.add(connection);
		return connection;
	}

	private Connection stopoverToEnd() {
		Connection connection = connection()
				.withId(2)
				.startsAt(stopover)
				.endsAt(end)
				.departsAt(departureStopoverToEnd)
				.arrivesAt(arrivalStopoverToEnd)
				.partOf(journey2)
				.build();
		journey1.add(connection);
		return connection;
	}

	private Connection stopoverToEndLater() {
		Connection connection = connection()
				.withId(3)
				.startsAt(stopover)
				.endsAt(end)
				.departsAt(departureStopoverToEndLater)
				.arrivesAt(arrivalStopoverToEndLater)
				.partOf(journey3)
				.build();
		journey2.add(connection);
		return connection;
	}

	private Stop start() {
		return stop().withId(1).withName("start").build();
	}

	private Stop stopover() {
		return stop()
				.withId(2)
				.withName("stopover")
				.minimumChangeTime(changeTime)
				.build();
	}

	private Stop end() {
		return stop().withId(3).withName("end").build();
	}

	@Test
	public void considerChangeTime() throws Exception {
		Time routeArrival = arrivalStopoverToEndLater;
		Profile profile = buildProfile();

		Store store = new InMemory();
		store.save(profile);
		RouteSearch search = new ProfileConnectionScan(store);

		Optional<PublicTransportRoute> route = search.findRoute(start, end, routeDeparture);

		assertThat(route, isPresent());
		assertThat(route, hasValue(arrivesAt(routeArrival)));
	}

	private Profile buildProfile() {
		ProfileBuilder builder = ProfileBuilder.from(connections);
		return builder.buildUpTo(end);
	}

	@Test
	public void useCorrectOrderOfConnectionsOfOneJourney() throws Exception {
		Stop start = stop().withId(1).build();
		Stop stop1 = stop().withId(2).build();
		Stop stop2 = stop().withId(3).build();
		Stop stop3 = stop().withId(4).build();
		Stop stop4 = stop().withId(5).build();
		Stop end = stop().withId(6).build();
		
		RelativeTime transferTime = of(0, MINUTES);
		end.addNeighbour(stop2, transferTime);
		stop2.addNeighbour(end, transferTime);
		
		ModifiableJourney journey = journey().withId(1).build();
		Time departure =         SimpleTime.ofSeconds(0);
		Time departureStartTo1 = departure;
		Time sameTime =   departureStartTo1.plus(of(1, MINUTES));
		
		Connection startTo1 = connection()
				.withId(1)
				.startsAt(start)
				.endsAt(stop1)
				.departsAt(departureStartTo1)
				.arrivesAt(sameTime)
				.partOf(journey)
				.build();
		Connection stop1To2 = connection()
				.withId(2)
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(sameTime)
				.arrivesAt(sameTime)
				.partOf(journey)
				.build();
		Connection stop2To3 = connection()
				.withId(3)
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(sameTime)
				.arrivesAt(sameTime)
				.partOf(journey)
				.build();
		Connection stop3To4 = connection()
				.withId(4)
				.startsAt(stop3)
				.endsAt(stop4)
				.departsAt(sameTime)
				.arrivesAt(sameTime)
				.partOf(journey)
				.build();
		
		journey.add(startTo1);
		journey.add(stop1To2);
		journey.add(stop2To3);
		journey.add(stop3To4);
		
		List<Connection> connections = asList(startTo1, stop1To2, stop2To3, stop3To4);
		Stop start2 = start;
		Stop end2 = end;
		ProfileBuilder builder = ProfileBuilder.from(connections);
		Profile profile = builder.buildUpTo(end2);
		InMemory store = new InMemory();
		store.save(profile);
		RouteSearch search = new ProfileConnectionScan(store);

		Optional<PublicTransportRoute> route = search.findRoute(start2, end2, departure);

		assertThat(route, isPresent());
		assertThat(route, hasValue(arrivesAt(sameTime)));
	}

}
