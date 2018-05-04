package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class WaitingVehiclesTest {

	private Vehicle vehicle;
	private Stop atStop;
	private WaitingVehicles waitingVehicles;

	@Before
	public void initialise() {
		vehicle = mock(Vehicle.class);
		atStop = Data.someStop();
		waitingVehicles = new WaitingVehicles();
	}
	
	@Test
	public void isWaiting() {
		waitingVehicles.wait(vehicle, atStop);
		boolean isWaiting = waitingVehicles.isWaiting(vehicle, atStop);
		
		assertTrue(isWaiting);
	}
	
	@Test
	public void isNotWaiting() {
		boolean isWaiting = waitingVehicles.isWaiting(vehicle, atStop);
		
		assertFalse(isWaiting);
	}
	
	@Test
	public void isNotWaitingAtOtherStop() {
		waitingVehicles.wait(vehicle, atStop);
		boolean isWaiting = waitingVehicles.isWaiting(vehicle, Data.anotherStop());
		
		assertFalse(isWaiting);
	}
	
	@Test
	public void severalWaitingAtSameStop() {
		Vehicle anotherVehicle = mock(Vehicle.class);
		waitingVehicles.wait(vehicle, atStop);
		waitingVehicles.wait(anotherVehicle, atStop);
		
		assertTrue(waitingVehicles.isWaiting(vehicle, atStop));
		assertTrue(waitingVehicles.isWaiting(anotherVehicle, atStop));
	}
	
	@Test
	public void vehicleStartsDriving() {
		waitingVehicles.wait(vehicle, atStop);
		waitingVehicles.drive(vehicle, atStop);
		
		assertFalse(waitingVehicles.isWaiting(vehicle, atStop));
	}
	
	@Test
	public void notWaitingVehicleCannotStart() {
		waitingVehicles.drive(vehicle, atStop);
		
		assertFalse(waitingVehicles.isWaiting(vehicle, atStop));
	}
}
