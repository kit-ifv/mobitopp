package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleEventTest {

	private Time earlyTime;
	private Time laterTime;
	private VehicleEvent earlyEvent;
	private VehicleEvent laterEvent;
	private VehicleEvent lowId;
	private VehicleEvent highId;

	@Before
	public void initialise() {
		earlyTime = Data.someTime();
		laterTime = earlyTime.plusMinutes(1);
		Vehicle vehicle = mock(Vehicle.class);
		earlyEvent = new VehicleEvent(earlyTime, vehicle);
		laterEvent = new VehicleEvent(laterTime, vehicle);
		lowId = new VehicleEvent(earlyTime, vehicle(0));
		highId = new VehicleEvent(earlyTime, vehicle(1));
	}
	
	private Vehicle vehicle(int id) {
		Vehicle vehicle = mock(Vehicle.class);
		when(vehicle.journeyId()).thenReturn(id);
		return vehicle;
	}

	@Test
	public void comparesTime() {
		assertThat(earlyEvent.compareTo(earlyEvent), is(equalTo(0)));
		assertThat(earlyEvent.compareTo(laterEvent), is(lessThan(0)));
		assertThat(laterEvent.compareTo(earlyEvent), is(greaterThan(0)));
	}
	
	@Test
	public void comparesVehicleId() {
		assertThat(lowId.compareTo(lowId), is(equalTo(0)));
		assertThat(lowId.compareTo(highId), is(lessThan(0)));
	}
}
