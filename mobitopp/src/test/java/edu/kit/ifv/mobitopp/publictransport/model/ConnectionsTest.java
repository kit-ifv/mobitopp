package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.awt.geom.Point2D;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ConnectionsTest {

	protected static final Point2D somePoint = new Point2D.Float();
	private static final Time someTime = Data.someTime();
	private static final RelativeTime anotherRelativeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime otherRelativeTime = RelativeTime.of(2, MINUTES);
	private Connections connections;
	private Stop stop1;
	private Stop stop2;
	private Stop stop3;
	private Stop stop4;
	private Connection stop1ToStop2;
	private Connection stop2ToStop3;
	private Connection stop3ToStop4;

	@Before
	public void initialise() {
		stop1 = someStop();
		stop2 = anotherStop();
		stop3 = otherStop();
		stop4 = yetAnotherStop();

		stop1ToStop2 = connection()
				.withId(1)
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
		stop2ToStop3 = connection()
				.withId(2)
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
		stop3ToStop4 = connection().startsAt(stop3).endsAt(stop4).build();
		connections = new Connections();
	}

	@Test
	public void calculateSingleConnection() throws Exception {
		connections.add(stop1ToStop2);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(stop1ToStop2));
	}

	@Test
	public void creationWithSeveralStopPointsOnRoute() throws Exception {
		connections.add(stop1ToStop2);
		connections.add(stop2ToStop3);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, hasItems(stop1ToStop2, stop2ToStop3));
	}

	@Test
	public void creationWithSameStopPointTwiceOnRoute() throws Exception {
		Connection stop2ToStop1 = connection()
				.withId(2)
				.startsAt(stop2)
				.endsAt(stop1)
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();

		connections.add(stop1ToStop2);
		connections.add(stop2ToStop1);
		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, hasItems(stop1ToStop2, stop2ToStop1));
	}

	@Test
	public void doesNotFilterConnectionsWhenNoConnectionIsAdded() throws Exception {
		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, is(empty()));
	}

	@Test
	public void doesNotFilterConnectionWithDifferentStartAndEndAndWhichArrivesAfterItDeparts()
			throws Exception {
		connections.add(stop1ToStop2);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(stop1ToStop2));
	}

	@Test
	public void filtersConnectionWithSameStartAndEnd() throws Exception {
		Stop sameStop = someStop();
		Connection sameStartAndEnd = connection().startsAt(someStop()).endsAt(sameStop).build();
		connections.add(stop1ToStop2);
		connections.add(sameStartAndEnd);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(stop1ToStop2));
	}

	@Test
	public void filtersConnectionWhichArrivesBeforeItDeparts() throws Exception {
		Time earlierTime = someTime();
		Time laterTime = oneMinuteLater();
		ConnectionBuilder differentStartAndEnd = connection()
				.startsAt(someStop())
				.endsAt(anotherStop());
		Connection arrivesBeforeItDeparts = differentStartAndEnd
				.but()
				.departsAt(laterTime)
				.arrivesAt(earlierTime)
				.build();
		Connection sameDepartureAndArrival = differentStartAndEnd
				.but()
				.departsAndArrivesAt(someTime())
				.build();
		connections.add(arrivesBeforeItDeparts);
		connections.add(sameDepartureAndArrival);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(sameDepartureAndArrival));
	}

	@Test
	public void appliesAllConnections() throws Exception {
		ConnectionConsumer consumer = mock(ConnectionConsumer.class);
		InOrder inOrder = inOrder(consumer);

		connections.add(stop1ToStop2);
		connections.add(stop2ToStop3);

		connections.apply(consumer);

		inOrder.verify(consumer).accept(stop1ToStop2);
		inOrder.verify(consumer).accept(stop2ToStop3);
	}

	@Test
	public void addAllConnections() throws Exception {
		ConnectionId id = ConnectionId.of(0);
		Connection connection = connection().withId(id).build();
		Connections newOnes = new Connections();
		newOnes.add(connection);
		Connections all = new Connections();

		all.addAll(newOnes);

		assertThat(all.get(id), is(equalTo(connection)));
	}

	@Test
	public void noConnectionAtAll() {
		assertThat(connections.hasNextAfter(stop1ToStop2), is(false));
	}

	@Test
	public void noConnectionAfterCurrent() {
		connections.add(stop1ToStop2);

		assertThat(connections.hasNextAfter(stop1ToStop2), is(false));
	}

	@Test
	public void hasConnectionAfterCurrent() {
		connections.add(stop1ToStop2);
		connections.add(stop2ToStop3);
		connections.add(stop3ToStop4);

		assertThat(connections.hasNextAfter(stop1ToStop2), is(true));
		assertThat(connections.hasNextAfter(stop2ToStop3), is(true));
	}

	@Test
	public void nextAfterIsAvailable() {
		connections.add(stop1ToStop2);
		connections.add(stop2ToStop3);
		connections.add(stop3ToStop4);

		Connection nextAfterFirst = connections.nextAfter(stop1ToStop2);
		Connection nextAfterSecond = connections.nextAfter(stop2ToStop3);

		assertThat(nextAfterFirst, is(stop2ToStop3));
		assertThat(nextAfterSecond, is(stop3ToStop4));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionWhenConnectionIsNotInCollection() {
		connections.add(stop1ToStop2);
		
		connections.nextAfter(stop2ToStop3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionOnMissingNextConnection() {
		connections.add(stop1ToStop2);

		connections.nextAfter(stop1ToStop2);
	}

	@Test
	public void positionOfConnection() {
		connections.add(stop1ToStop2);
		connections.add(stop2ToStop3);
		connections.add(stop3ToStop4);

		int positionOfFirst = connections.positionOf(stop1ToStop2);
		int positionOfSecond = connections.positionOf(stop2ToStop3);
		int positionOfThird = connections.positionOf(stop3ToStop4);

		assertThat(positionOfFirst, is(0));
		assertThat(positionOfSecond, is(1));
		assertThat(positionOfThird, is(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failWhenConnectionIsNotIncluded() {
		Connection connection = connection().build();

		connections.positionOf(connection);
	}

	private Time someTime() {
		return someTime;
	}

	private Time oneMinuteLater() {
		return someTime.plus(anotherRelativeTime);
	}

	private Time twoMinutesLater() {
		return someTime.plus(otherRelativeTime);
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Journey oneJourney = journey().build();
		Journey anotherJourney = journey().withId(1).build();
		Connection oneConnection = connection().startsAt(someStop()).build();
		Connection anotherConnection = connection().startsAt(anotherStop()).build();
		EqualsVerifier
				.forClass(Connections.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withPrefabValues(Journey.class, oneJourney, anotherJourney)
				.withPrefabValues(Connection.class, oneConnection, anotherConnection)
				.withOnlyTheseFields("connections")
				.usingGetClass()
				.verify();
	}
}
