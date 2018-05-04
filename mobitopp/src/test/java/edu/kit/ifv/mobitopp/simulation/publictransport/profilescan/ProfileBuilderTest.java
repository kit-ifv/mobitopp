package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromOtherToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.threeMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.time.RelativeTime.of;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ProfileBuilderTest {

	private static final RelativeTime walkTime = RelativeTime.of(1, MINUTES);

	@Test
	public void buildsUpNoProfilesWithoutConnections() throws Exception {
		ProfileBuilder builder = buildFrom();

		Profile profile = builder.buildUpTo(anotherStop());

		assertThat(profile.size(), is(0));
	}

	@Test
	public void buildsUpSingleProfileForSingleConnection() throws Exception {
		ProfileBuilder builder = buildFrom(fromSomeToAnother());

		Profile profile = builder.buildUpTo(anotherStop());

		assertThat(profile.from(someStop()), is(equalTo(fromSome())));
	}

	private static ArrivalTimeFunction profile(Connection connection) {
		ArrivalTimeFunction function = new ArrivalTimeFunction();
		function.update(profileEntry(connection.departure(), connection.arrival(), connection));
		return function;
	}

	@Test
	public void buildsUpSingleProfileForSeveralConnectionsDirectlyToTargetStop() throws Exception {
		ProfileBuilder builder = buildFrom(laterFromSomeToAnother(), fromSomeToAnother());

		Profile profile = builder.buildUpTo(anotherStop());

		assertThat(profile.from(someStop()), is(equalTo(sameStart())));
	}

	@Test
	public void buildsUpSeveralProfilesForSeveralConnectionsFromDifferentStopsDirectlyToTargetStop()
			throws Exception {
		ProfileBuilder builder = buildFrom(fromOtherToAnother(), fromSomeToAnother());

		Profile profile = builder.buildUpTo(anotherStop());

		assertThat(profile.from(someStop()), is(equalTo(fromSome())));
		assertThat(profile.from(otherStop()), is(equalTo(fromOther())));
	}

	@Test
	public void buildsUpProfilesOnlyToTargetStop() throws Exception {
		ProfileBuilder builder = buildFrom(fromAnotherToOther(), fromSomeToAnother());

		Profile profile = builder.buildUpTo(anotherStop());

		assertThat(profile.from(someStop()), is(equalTo(fromSome())));
		assertThat(profile.hasProfileTo(anotherStop()), is(false));
	}

	@Test
	public void buildsUpProfilesForConnectionsWithStopoverAndChangetime() throws Exception {
		ProfileBuilder builder = buildFrom(fromAnotherToOther(), fromSomeToAnother());

		Profile profile = builder.buildUpTo(otherStop());

		assertThat(profile.from(someStop()), is(equalTo(fromSomeWithStopover())));
		assertThat(profile.from(anotherStop()), is(equalTo(fromAnotherViaStopover())));
	}

	@Test
	public void buildsUpProfilesFromWronglyOrderedConnections() throws Exception {
		ProfileBuilder builder = buildFrom(fromSomeToAnother(), fromAnotherToOther());

		Profile profile = builder.buildUpTo(otherStop());

		assertThat(profile.from(someStop()), is(equalTo(fromSomeWithStopover())));
		assertThat(profile.from(anotherStop()), is(equalTo(fromAnotherViaStopover())));
	}

	@Test
	public void buildsUpProfilesForConnectionsWithStopoverFromSameTrip() throws Exception {
		ProfileBuilder builder = buildFrom(onSameTripFromAnotherToOther(),
				onSameTripFromSomeToAnother());

		Profile profile = builder.buildUpTo(otherStop());

		assertThat(profile.from(someStop()), is(equalTo(fromSomeWithStopoverOnSameJourney())));
		assertThat(profile.from(anotherStop()), is(equalTo(fromAnotherOnSameJourney())));
	}

	@Test
	public void arrivesTargetViaFootpath() throws Exception {
		Stop someNeighbour = stop().withId(1).build();
		Stop anotherNeighbour = stop().withId(2).build();
		someNeighbour.addNeighbour(anotherNeighbour, of(1, MINUTES));
		Stop startOfConnection = stop().withId(3).build();
		Connection connectionToFootpath = connectionBetween(startOfConnection, someNeighbour);

		ProfileBuilder builder = buildFrom(connectionToFootpath);
		Profile profile = builder.buildUpTo(anotherNeighbour);

		assertThat(profile.from(startOfConnection), is(equalTo(reachableVia(connectionToFootpath))));
	}

	@Test
	public void arrivesViaIntermediateFootpath() throws Exception {
		Stop start = stop().withId(1).build();
		Stop footStart = stop().withId(2).build();
		Stop footEnd = stop().withId(3).build();
		Stop end = stop().withId(4).build();
		footStart.addNeighbour(footEnd, walkTime);
		footEnd.addNeighbour(footStart, walkTime);

		Connection toFootStart = connection()
				.startsAt(start)
				.endsAt(footStart)
				.departsAndArrivesAt(someTime())
				.partOf(someJourney())
				.build();
		Connection fromFootEnd = connection()
				.startsAt(footEnd)
				.endsAt(end)
				.departsAndArrivesAt(twoMinutesLater())
				.partOf(anotherJourney())
				.build();
		ProfileBuilder builder = buildFrom(toFootStart, fromFootEnd);

		Profile profile = builder.buildUpTo(end);

		assertThat(profile.from(start), is(equalTo(functionFor(toFootStart))));
	}

	private static ProfileBuilder buildFrom(Connection... connections) {
		return ProfileBuilder.from(asList(connections));
	}

	private static ArrivalTimeFunction functionFor(Connection toFootStart) {
		ArrivalTimeFunction function = new ArrivalTimeFunction();
		function.update(profileEntry(someTime(), twoMinutesLater(), toFootStart));
		return function;
	}

	private static ArrivalTimeFunction reachableVia(Connection connectionToFootpath) {
		ArrivalTimeFunction function = new ArrivalTimeFunction();
		function.update(profileEntry(oneMinuteLater(), threeMinutesLater(), connectionToFootpath));
		return function;
	}

	private static Connection connectionBetween(Stop start, Stop end) {
		return connection()
				.startsAt(start)
				.endsAt(end)
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private static ArrivalTimeFunction fromSome() {
		return profile(fromSomeToAnother());
	}

	private static ArrivalTimeFunction sameStart() {
		Connection firstConnection = fromSomeToAnother();
		Connection secondConnection = laterFromSomeToAnother();
		ArrivalTimeFunction function = new ArrivalTimeFunction();
		function.update(
				profileEntry(secondConnection.departure(), secondConnection.arrival(), secondConnection));
		function.update(
				profileEntry(firstConnection.departure(), firstConnection.arrival(), firstConnection));
		return function;
	}

	private static ArrivalTimeFunction fromOther() {
		return profile(fromOtherToAnother());
	}

	private static Connection fromSomeToAnother() {
		return connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	private static Connection laterFromSomeToAnother() {
		return connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private static Connection fromAnotherToOther() {
		return connection()
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(twoMinutesLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	private static Stop someStop() {
		return stop()
				.withId(0)
				.withName("some stop")
				.minimumChangeTime(walkTime)
				.build();
	}

	private static Stop anotherStop() {
		return stop()
				.withId(1)
				.withName("another stop")
				.minimumChangeTime(walkTime)
				.build();
	}

	private static ArrivalTimeFunction fromAnotherViaStopover() {
		ArrivalTimeFunction function = new ArrivalTimeFunction();
		function.update(profileEntry(twoMinutesLater(), threeMinutesLater(), fromAnotherToOther()));
		return function;
	}

	private static ArrivalTimeFunction fromSomeWithStopover() {
		ArrivalTimeFunction function = new ArrivalTimeFunction();
		function.update(profileEntry(fromSomeToAnother().departure(), fromAnotherToOther().arrival(),
				fromSomeToAnother()));
		return function;
	}

	private static Connection onSameTripFromAnotherToOther() {
		return connection()
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(someJourney())
				.build();
	}

	private static Connection onSameTripFromSomeToAnother() {
		return connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney())
				.build();
	}

	private static Journey someJourney() {
		return journey().withId(1).build();
	}

	private static Journey anotherJourney() {
		return journey().withId(2).build();
	}

	private static ArrivalTimeFunction fromSomeWithStopoverOnSameJourney() {
		ArrivalTimeFunction function = new ArrivalTimeFunction();
		function.update(profileEntry(onSameTripFromSomeToAnother().departure(),
				onSameTripFromAnotherToOther().arrival(), onSameTripFromSomeToAnother()));
		return function;
	}

	private static ArrivalTimeFunction fromAnotherOnSameJourney() {
		return profile(onSameTripFromAnotherToOther());
	}

	private static FunctionEntry profileEntry(Time departure, Time arrival, Connection startingWith) {
		return new FunctionEntry(departure, arrival, startingWith);
	}
}
