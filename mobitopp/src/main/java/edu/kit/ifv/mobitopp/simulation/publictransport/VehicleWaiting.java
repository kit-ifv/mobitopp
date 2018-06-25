package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

class VehicleWaiting {

	private final RelativeTime waitTime;

	public VehicleWaiting(RelativeTime waitTime) {
		super();
		this.waitTime = waitTime;
	}

	public Time nextDeparture(Time current) {
		return current.plus(waitTime);
	}

}