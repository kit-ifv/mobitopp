package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fourMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleTimesTest {

	private VehicleTimes vehicleConnections;
	private Connection firstConnection;
	private Connection secondConnection;

	@Before
	public void initialise() {
		firstConnection = Data.fromSomeToAnother();
		secondConnection = longFromAnotherToOther();
		Collection<Connection> connections = asList(firstConnection, secondConnection);
		vehicleConnections = VehicleTimesConverter.from(connections);
	}

	public static Connection longFromAnotherToOther() {
		return connection()
				.withId(7)
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(twoMinutesLater())
				.arrivesAt(fourMinutesLater())
				.build();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failForEmptyConnections() {
		VehicleTimesConverter.from(Collections.emptyList());
	}

	@Test
	public void switchesDepartureTime() {
		Optional<Time> start = vehicleConnections.nextDeparture();
		vehicleConnections.move(firstConnection.arrival());
		Optional<Time> intermediate = vehicleConnections.nextDeparture();
		vehicleConnections.move(secondConnection.arrival());
		Optional<Time> end = vehicleConnections.nextDeparture();

		assertThat(start, hasValue(firstConnection.departure()));
		assertThat(intermediate, hasValue(secondConnection.departure()));
		assertThat(end, isEmpty());
	}

	@Test
	public void switchesArrivalTime() {
		Optional<Time> start = vehicleConnections.nextArrival();
		vehicleConnections.move(firstConnection.arrival());
		Optional<Time> intermediate = vehicleConnections.nextArrival();
		vehicleConnections.move(secondConnection.arrival());
		Optional<Time> end = vehicleConnections.nextArrival();

		assertThat(start, hasValue(firstConnection.arrival()));
		assertThat(intermediate, hasValue(secondConnection.arrival()));
		assertThat(end, isEmpty());
	}

	@Test
	public void stayAtSameDeparture() {
		Optional<Time> start = vehicleConnections.nextDeparture();
		Optional<Time> end = vehicleConnections.nextDeparture();

		assertThat(start, is(equalTo(end)));
	}
	
	@Test
	public void switchConnectionId() {
		Optional<ConnectionId> start = vehicleConnections.nextConnection();
		vehicleConnections.move(firstConnection.arrival());
		Optional<ConnectionId> intermediate = vehicleConnections.nextConnection();
		vehicleConnections.move(secondConnection.arrival());
		Optional<ConnectionId> end = vehicleConnections.nextConnection();
		
		assertThat(start, hasValue(firstConnection.id()));
		assertThat(intermediate, hasValue(secondConnection.id()));
		assertThat(end, isEmpty());
	}

}
