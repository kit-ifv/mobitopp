package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.time.Time;

public class DepartedVehiclesTest {

	@Test
	public void vehicleHasDeparted() {
		Vehicle vehicle = mock(Vehicle.class);
		when(vehicle.nextConnection()).thenReturn(Optional.of(someConnection()));
		PublicTransportLeg leg = newLeg();
		DepartedVehicles vehicles = new DepartedVehicles();

		vehicles.add(vehicle);
		boolean vehicleHasDeparted = vehicles.hasDeparted(leg);

		assertTrue(vehicleHasDeparted);
	}

	@Test
	public void vehicleHasNotDeparted() {
		Vehicle vehicle = mock(Vehicle.class);
		PublicTransportLeg leg = newLeg();
		DepartedVehicles vehicles = new DepartedVehicles();

		vehicles.add(vehicle);
		boolean vehicleHasDeparted = vehicles.hasDeparted(leg);

		assertFalse(vehicleHasDeparted);
	}
	
	@Test
	public void ensureLateVehiclesAreMatched() {
		ConnectionBuilder builder = connection().withId(1);
		Connection plannedConnection = builder.departsAndArrivesAt(Data.someTime()).build();
		Optional<Connection> lateConnection = Optional.of(builder.departsAndArrivesAt(Data.oneMinuteLater()).build());
		Vehicle vehicle = mock(Vehicle.class);
		when(vehicle.nextConnection()).thenReturn(lateConnection);
		PublicTransportLeg leg = newLeg(plannedConnection);
		DepartedVehicles vehicles = new DepartedVehicles();

		vehicles.add(vehicle);
		boolean vehicleHasDeparted = vehicles.hasDeparted(leg);

		assertTrue(vehicleHasDeparted);		
	}

	private PublicTransportLeg newLeg() {
		return newLeg(someConnection());
	}

	private PublicTransportLeg newLeg(Connection connection) {
		Stop start = connection.start();
		Stop end = connection.end();
		Journey journey = connection.journey();
		Time departure = connection.departure();
		Time arrival = connection.arrival();
		List<Connection> connections = asList(connection);
		return new PublicTransportLeg(start, end, journey, departure, arrival, connections);
	}

	private Connection someConnection() {
		return Data.fromSomeToAnother();
	}
}
