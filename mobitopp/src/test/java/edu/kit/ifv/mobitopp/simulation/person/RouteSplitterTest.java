package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.second;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.ScannedRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class RouteSplitterTest {

	private ModifiableJourney someJourney;
	private ModifiableJourney anotherJourney;
	
	@Before
	public void initialise() throws Exception {
		someJourney = journey().withId(1).build();
		anotherJourney = journey().withId(2).build();
	}

	@Test
	public void splitsNothingWhenNoConnectionsAreAvailable() throws Exception {
		PublicTransportRoute route = tourWithoutConnections();

		List<PublicTransportLeg> parts = splitInParts(route);

		assertThat(parts, is(empty()));
	}

	@Test
	public void splitsTourWithOneConnectionIntoOnePart() throws Exception {
		PublicTransportRoute route = tourWithSingleConnection();

		List<PublicTransportLeg> part = splitInParts(route);

		assertThat(part, hasSize(1));
		PublicTransportLeg firstPart = part.get(0);
		assertThat(firstPart, startsAt(someStop()));
		assertThat(firstPart, endsAt(anotherStop()));
		assertThat(firstPart, uses(someJourney));
		assertThat(firstPart, uses(asList(singleConnection())));
	}

	@Test
	public void splitsTourWithTwoConnectionsAndTwoJourneysIntoTwoParts() throws Exception {
		PublicTransportRoute route = tourWithTwoConnections();

		List<PublicTransportLeg> part = splitInParts(route);

		assertThat(part, hasSize(2));
		PublicTransportLeg firstPart = part.get(0);
		assertThat("first part", firstPart, startsAt(someStop()));
		assertThat("first part", firstPart, endsAt(otherStop()));
		assertThat("first part", firstPart, uses(someJourney));
		assertThat("first part", firstPart, uses(asList(connectionOnSomeJourney())));

		PublicTransportLeg secondPart = part.get(1);
		assertThat("second part", secondPart, startsAt(otherStop()));
		assertThat("second part", secondPart, endsAt(anotherStop()));
		assertThat("second part", secondPart, uses(anotherJourney));
		assertThat("second part", secondPart, uses(asList(connectionOnAnotherJourney())));
	}

	@Test
	public void splitsTourWithTwoConnectionsOnTheSameJourneyIntoOnePart() throws Exception {
		PublicTransportRoute route = tourWithTwoConnectionsOnTheSameJourney();

		List<PublicTransportLeg> part = splitInParts(route);

		assertThat(part, hasSize(1));
		PublicTransportLeg firstPart = part.get(0);
		assertThat(firstPart, startsAt(someStop()));
		assertThat(firstPart, endsAt(anotherStop()));
		assertThat(firstPart, uses(someJourney));
		assertThat(firstPart,
				uses(asList(connectionOnSomeJourney(), anotherConnectionOnSomeJourney())));
	}

	private static List<PublicTransportLeg> splitInParts(PublicTransportRoute route) {
		return RouteSplitter.splitInParts(route);
	}

	private PublicTransportRoute tourWithSingleConnection() {
		Connection connection = singleConnection();
		List<Connection> connections = asList(connection);

		return new ScannedRoute(someStop(), anotherStop(), someTime(), oneMinuteLater(), connections);
	}

	private Connection singleConnection() {
		Connection connection = connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney)
				.build();
		return connection;
	}

	private static PublicTransportRoute tourWithoutConnections() {
		return new ScannedRoute(someStop(), anotherStop(), someTime(), oneMinuteLater(),
				Collections.emptyList());
	}

	private PublicTransportRoute tourWithTwoConnections() {
		List<Connection> connections = new ArrayList<>();
		Connection startToIntermediate = connectionOnSomeJourney();
		Connection intermediateToEnd = connectionOnAnotherJourney();
		connections.add(startToIntermediate);
		connections.add(intermediateToEnd);

		PublicTransportRoute tour = new ScannedRoute(someStop(), anotherStop(), someTime(),
				twoMinutesLater(), connections);
		return tour;
	}

	private PublicTransportRoute tourWithTwoConnectionsOnTheSameJourney() {
		List<Connection> connections = new ArrayList<>();
		Connection startToIntermediate = connectionOnSomeJourney();
		Connection intermediateToEnd = anotherConnectionOnSomeJourney();
		connections.add(startToIntermediate);
		connections.add(intermediateToEnd);

		PublicTransportRoute tour = new ScannedRoute(someStop(), anotherStop(), someTime(),
				twoMinutesLater(), connections);
		return tour;
	}

	private static Time someTime() {
		return second(10);
	}

	private static Time oneMinuteLater() {
		return second(70);
	}

	private static Time twoMinutesLater() {
		return second(130);
	}

	private Connection connectionOnAnotherJourney() {
		Connection intermediateToEnd = connection()
				.startsAt(otherStop())
				.endsAt(anotherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(anotherJourney)
				.build();
		return intermediateToEnd;
	}

	private Connection connectionOnSomeJourney() {
		Connection startToIntermediate = connection()
				.startsAt(someStop())
				.endsAt(otherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney)
				.build();
		return startToIntermediate;
	}

	private Connection anotherConnectionOnSomeJourney() {
		Connection intermediateToEnd = connection()
				.startsAt(otherStop())
				.endsAt(anotherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(someJourney)
				.build();
		return intermediateToEnd;
	}

	private static Matcher<PublicTransportLeg> startsAt(Stop start) {
		return new TypeSafeMatcher<PublicTransportLeg>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("starts at ");
				description.appendValue(start);
			}

			@Override
			protected boolean matchesSafely(PublicTransportLeg part) {
				return start.equals(part.start());
			}

			@Override
			protected void describeMismatchSafely(
					PublicTransportLeg part, Description mismatchDescription) {
				mismatchDescription.appendText("starts at ");
				mismatchDescription.appendValue(part.start());
			}
		};
	}

	private static Matcher<PublicTransportLeg> endsAt(Stop end) {
		return new TypeSafeMatcher<PublicTransportLeg>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("ends at ");
				description.appendValue(end);
			}

			@Override
			protected boolean matchesSafely(PublicTransportLeg part) {
				return end.equals(part.end());
			}

			@Override
			protected void describeMismatchSafely(
					PublicTransportLeg part, Description mismatchDescription) {
				mismatchDescription.appendText("ends at ");
				mismatchDescription.appendValue(part.end());
			}
		};
	}

	private static Matcher<PublicTransportLeg> uses(Journey journey) {
		return new TypeSafeMatcher<PublicTransportLeg>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("uses ");
				description.appendValue(journey);
			}

			@Override
			protected boolean matchesSafely(PublicTransportLeg part) {
				return journey.equals(part.journey());
			}

			@Override
			protected void describeMismatchSafely(
					PublicTransportLeg part, Description mismatchDescription) {
				mismatchDescription.appendText("uses ");
				mismatchDescription.appendValue(part.journey());
			}
		};
	}

	private static Matcher<PublicTransportLeg> uses(List<Connection> connections) {
		return new TypeSafeMatcher<PublicTransportLeg>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("uses ");
				description.appendValue(connections);
			}

			@Override
			protected boolean matchesSafely(PublicTransportLeg leg) {
				return connections.equals(leg.connections());
			}

			@Override
			protected void describeMismatchSafely(
					PublicTransportLeg leg, Description mismatchDescription) {
				mismatchDescription.appendText("uses ");
				mismatchDescription.appendValue(leg.connections());
			}
		};
	}
}
