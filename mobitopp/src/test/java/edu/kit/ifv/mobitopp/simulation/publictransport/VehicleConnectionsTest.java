package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleConnectionsTest {

	private VehicleConnections vehicleConnections;
	private Connection firstConnection;
	private Connection secondConnection;

	@Before
	public void initialise() {
		firstConnection = Data.fromSomeToAnother();
		secondConnection = Data.fromAnotherToOther();
		Collection<Connection> connections = asList(firstConnection, secondConnection);
		vehicleConnections = new VehicleConnections(connections);
	}

	@Test
	public void switchesConnections() {
		Optional<Time> start = vehicleConnections.nextDeparture();
		vehicleConnections.move();
		Optional<Time> intermediate = vehicleConnections.nextDeparture();
		vehicleConnections.move();
		Optional<Time> end = vehicleConnections.nextDeparture();

		assertThat(start, hasValue(firstConnection.departure()));
		assertThat(intermediate, hasValue(secondConnection.departure()));
		assertThat(end, isEmpty());
	}

	@Test
	public void switchesArrivalTimes() {
		Optional<Time> start = vehicleConnections.nextArrival();
		vehicleConnections.move();
		Optional<Time> intermediate = vehicleConnections.nextArrival();
		vehicleConnections.move();
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
	public void switchConnections() {
		Optional<ConnectionId> start = vehicleConnections.nextConnection();
		vehicleConnections.move();
		Optional<ConnectionId> intermediate = vehicleConnections.nextConnection();
		vehicleConnections.move();
		Optional<ConnectionId> end = vehicleConnections.nextConnection();
		
		assertThat(start, hasValue(firstConnection.id()));
		assertThat(intermediate, hasValue(secondConnection.id()));
		assertThat(end, isEmpty());
	}

}
