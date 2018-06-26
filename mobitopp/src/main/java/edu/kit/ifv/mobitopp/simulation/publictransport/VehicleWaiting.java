package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleWaiting {

	private final RelativeTime waitTime;

	public VehicleWaiting(RelativeTime waitTime) {
		super();
		this.waitTime = waitTime;
	}

	public Time nextDeparture(Time current) {
		return current.plus(waitTime);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((waitTime == null) ? 0 : waitTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleWaiting other = (VehicleWaiting) obj;
		if (waitTime == null) {
			if (other.waitTime != null)
				return false;
		} else if (!waitTime.equals(other.waitTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [waitTime=" + waitTime + "]";
	}

}