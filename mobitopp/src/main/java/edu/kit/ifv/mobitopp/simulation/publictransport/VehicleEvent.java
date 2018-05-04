package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleEvent implements Comparable<VehicleEvent> {

	private final Time time;
	private final Vehicle vehicle;

	public VehicleEvent(Time nextProcessingTime, Vehicle vehicle) {
		this.time = nextProcessingTime;
		this.vehicle = vehicle;
	}

	@Override
	public int compareTo(VehicleEvent other) {
		if (0 != time.compareTo(other.time)) {
			return time.compareTo(other.time);
		}
		return Integer.compare(vehicle.journeyId(), other.vehicle.journeyId());
	}

	public Vehicle vehicle() {
		return vehicle;
	}

	public Time time() {
		return time;
	}

}
