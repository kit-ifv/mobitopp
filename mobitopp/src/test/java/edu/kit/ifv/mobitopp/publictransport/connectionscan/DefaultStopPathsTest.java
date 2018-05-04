package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class DefaultStopPathsTest {

	@Test
	public void knowsDistanceToStops() {
		StopPath asExpected = shortDistance();
		StopPaths stops = stops(asList(asExpected));

		StopPath distance = stops.pathTo(nearStop());

		assertThat(distance, is(asExpected));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsForUnknownStop() {
		StopPath knownStop = shortDistance();
		Stop unknownStop = farStop();
		StopPaths stops = stops(asList(knownStop));

		stops.pathTo(unknownStop);
	}

	@Test
	public void considersPathToStopToFindCorrectStartStop() throws Exception {
		StopPaths stops = stops(asList(shortDistance(), longDistance()));
		Connection connection = mock(Connection.class);
		when(connection.departure()).thenReturn(oneMinuteLater());
		Time time = someTime();

		assertTrue(stops.isConnectionReachableAt(nearStop(), time, connection));
		assertFalse(stops.isConnectionReachableAt(farStop(), time, connection));
	}
	
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(DefaultStopPaths.class)
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withOnlyTheseFields("stopPaths")
				.usingGetClass()
				.verify();
	}

	private static StopPaths stops(List<StopPath> stops) {
		return DefaultStopPaths.from(stops);
	}

	private StopPath shortDistance() {
		return new StopPath(nearStop(), shortDuration());
	}

	private StopPath longDistance() {
		return new StopPath(farStop(), longDuration());
	}

	private RelativeTime shortDuration() {
		return RelativeTime.of(1, MINUTES);
	}

	private Stop nearStop() {
		return someStop();
	}

	private RelativeTime longDuration() {
		return RelativeTime.of(2, MINUTES);
	}

	private Stop farStop() {
		return anotherStop();
	}
}
