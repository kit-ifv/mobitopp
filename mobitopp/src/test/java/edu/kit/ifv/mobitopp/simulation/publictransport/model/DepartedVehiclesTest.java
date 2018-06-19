package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;

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
		PublicTransportLeg leg = plannedLeg();
		Vehicle vehicle = lateVehicle();
		DepartedVehicles vehicles = new DepartedVehicles();

		vehicles.add(vehicle);
		boolean vehicleHasDeparted = vehicles.hasDeparted(leg);

		assertTrue(vehicleHasDeparted);
	}

	private Vehicle lateVehicle() {
		Vehicle vehicle = mock(Vehicle.class);
		Optional<ConnectionId> lateConnection = Optional.of(someConnection());
		when(vehicle.nextConnection()).thenReturn(lateConnection);
		return vehicle;
	}

	private PublicTransportLeg plannedLeg() {
		ConnectionId plannedConnection = someConnection();
		PublicTransportLeg leg = newLeg(plannedConnection);
		return leg;
	}

	private PublicTransportLeg newLeg() {
		return newLeg(someConnection());
	}

	private PublicTransportLeg newLeg(ConnectionId connection) {
		PublicTransportLeg leg = mock(PublicTransportLeg.class);
		when(leg.firstConnection()).thenReturn(connection);
		return leg;
	}

	private ConnectionId someConnection() {
		return ConnectionId.of(1);
	}
}
