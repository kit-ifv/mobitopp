package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder;

public class DefaultUsedConnectionsTest {

	private static final int maximumNumberOfStops = 4;
	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Test
	public void connectionsFromStartToEndWithOneAvailableConnection() throws Exception {
		UsedConnections arrivals = new DefaultUsedConnections(maximumNumberOfStops);
		arrivals.update(anotherStop(), from1To2());

		List<Connection> connections = arrivals.collectConnections(someStop(), anotherStop());

		assertThat(connections, is(equalTo(Arrays.asList(from1To2()))));
	}

	@Test
	public void connectionsFromStartToEndWithMultipleAvailableConnections() throws Exception {
		UsedConnections arrivals = new DefaultUsedConnections(maximumNumberOfStops);
		arrivals.update(anotherStop(), from1To2());
		arrivals.update(otherStop(), from2To3());

		List<Connection> connections = arrivals.collectConnections(someStop(), otherStop());

		assertThat(connections, is(equalTo(Arrays.asList(from1To2(), from2To3()))));
	}

	@Test
	public void connectionsFromStartToEndWithUnusedConnections() throws Exception {
		UsedConnections arrivals = new DefaultUsedConnections(maximumNumberOfStops);
		arrivals.update(anotherStop(), from1To2());
		arrivals.update(otherStop(), from2To3());
		arrivals.update(yetAnotherStop(), from3To4());

		List<Connection> connections = arrivals.collectConnections(someStop(), otherStop());

		assertThat(connections, is(equalTo(Arrays.asList(from1To2(), from2To3()))));
	}

	@Test
	public void buildsUpConnectionsToSeveralStartStops() throws Exception {
		StopPaths start = mock(StopPaths.class);
		when(start.isConnectionReachableAt(someStop(), someTime(), from1To2())).thenReturn(true);
		when(start.isConnectionReachableAt(yetAnotherStop(), someTime(), from4To2())).thenReturn(false);
		when(start.stops()).thenReturn(asList(someStop(), yetAnotherStop()));
		UsedConnections arrivals = new DefaultUsedConnections(maximumNumberOfStops);
		arrivals.update(anotherStop(), from1To2());
		arrivals.update(otherStop(), from2To3());

		List<Connection> connections = arrivals.collectConnections(start, otherStop(), someTime());

		assertThat(connections, is(equalTo(asList(from1To2(), from2To3()))));
	}

	@Test
	public void buildsUpConnectionsToSeveralStartStopsTriangulation() throws Exception {
		StopPaths start = mock(StopPaths.class);
		when(start.stops()).thenReturn(asList(yetAnotherStop(), someStop()));
		when(start.isConnectionReachableAt(someStop(), someTime(), from1To2())).thenReturn(true);
		when(start.isConnectionReachableAt(yetAnotherStop(), someTime(), from4To2())).thenReturn(false);
		UsedConnections arrivals = new DefaultUsedConnections(maximumNumberOfStops);
		arrivals.update(anotherStop(), from1To2());
		arrivals.update(otherStop(), from2To3());

		List<Connection> connections = arrivals.collectConnections(start, otherStop(), someTime());

		assertThat(connections, is(equalTo(asList(from1To2(), from2To3()))));
	}

	@Test
	public void buildsUpConnectionsToSeveralStartStopsWithFootpaths() throws Exception {
		StopPaths start = mock(StopPaths.class);
		when(start.stops()).thenReturn(asList(yetAnotherStop(), someStop()));
		when(start.isConnectionReachableAt(yetAnotherStop(), someTime(), from4To1WithFootpath())).thenReturn(true);
		when(start.isConnectionReachableAt(someStop(), someTime(), from1To2WithFootpath())).thenReturn(false);
		UsedConnections arrivals = new DefaultUsedConnections(maximumNumberOfStops);
		arrivals.update(anotherStop(), from1To2WithFootpath());
		arrivals.update(otherStop(), from2To3());
		arrivals.update(someStop(), from4To1WithFootpath());

		List<Connection> connections = arrivals.collectConnections(start, otherStop(), someTime());

		assertThat(connections,
				is(equalTo(asList(from4To1WithFootpath(), from1To2WithFootpath(), from2To3()))));
	}

	private Connection from1To2WithFootpath() {
		return buildFrom1To2().departsAndArrivesAt(twoMinutesLater()).build();
	}

	private Connection from4To1WithFootpath() {
		return connection()
				.startsAt(yetAnotherStop())
				.endsAt(someStop())
				.departsAndArrivesAt(oneMinuteLater())
				.build();
	}

	private Connection from1To2() {
		return buildFrom1To2().departsAndArrivesAt(someTime()).build();
	}

	private ConnectionBuilder buildFrom1To2() {
		return connection().startsAt(someStop()).endsAt(anotherStop());
	}

	private Connection from2To3() {
		return connection()
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAndArrivesAt(oneMinuteLater())
				.build();
	}

	private Connection from3To4() {
		return connection()
				.startsAt(otherStop())
				.endsAt(yetAnotherStop())
				.departsAndArrivesAt(twoMinutesLater())
				.build();
	}

	private Connection from4To2() {
		return buildFrom4To2().departsAndArrivesAt(twoMinutesLater()).build();
	}

	private ConnectionBuilder buildFrom4To2() {
		return connection().startsAt(yetAnotherStop()).endsAt(anotherStop());
	}

	@Test
	public void noConnectionAvailable() throws Exception {
		int numberOfStops = 2;
		UsedConnections arrivals = new DefaultUsedConnections(numberOfStops);

		expected.expect(StopNotReachable.class);
		arrivals.collectConnections(someStop(), anotherStop());
	}
}
