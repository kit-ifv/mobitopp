package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.second;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.geom.Point2D;
import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ScannedRouteTest {

	@Test
	public void duration() throws Exception {
		PublicTransportRoute tour = tourWithSingleConnection();
		RelativeTime duration = tour.duration();

		assertThat(duration, is(RelativeTime.of(60, SECONDS)));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Stop oneStop = someStop();
		Stop anotherStop = anotherStop();
		Journey oneJourney = someJourney();
		Journey anotherJourney = anotherJourney();
		Connection oneConnection = connection().startsAt(oneStop).build();
		Connection anotherConnection = connection().startsAt(anotherStop).build();
		EqualsVerifier
				.forClass(ScannedRoute.class)
				.withPrefabValues(Time.class, someTime(), oneMinuteLater())
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, oneStop, anotherStop)
				.withPrefabValues(Journey.class, oneJourney, anotherJourney)
				.withPrefabValues(Connection.class, oneConnection, anotherConnection)
				.usingGetClass()
				.verify();
	}

	private static PublicTransportRoute tourWithSingleConnection() {
		Connection connection = singleConnection();
		List<Connection> connections = asList(connection);

		return new ScannedRoute(someStop(), anotherStop(), someTime(), oneMinuteLater(), connections);
	}

	private static Connection singleConnection() {
		Connection connection = connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney())
				.build();
		return connection;
	}

	private static Time someTime() {
		return second(10);
	}

	private static Time oneMinuteLater() {
		return second(70);
	}

	private static Journey someJourney() {
		return journey().withId(1).build();
	}

	private static Journey anotherJourney() {
		return journey().withId(2).build();
	}
}
