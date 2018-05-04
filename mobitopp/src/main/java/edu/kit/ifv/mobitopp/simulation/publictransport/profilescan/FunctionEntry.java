package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class FunctionEntry {

	private final Time departure;
	private final Time arrivalAtTarget;
	private final Connection connection;

	public FunctionEntry(Time departure, Time arrivalAtTarget, Connection connection) {
		super();
		this.departure = departure;
		this.arrivalAtTarget = arrivalAtTarget;
		this.connection = connection;
	}

	Time departure() {
		return departure;
	}

	Time arrivalAtTarget() {
		return arrivalAtTarget;
	}

	public Connection connection() {
		return connection;
	}

	public FunctionEntry clearChangeTimeAt(Stop start) {
		Time withoutChangeTime = start.addChangeTimeTo(departure);
		return new FunctionEntry(withoutChangeTime, arrivalAtTarget, connection);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrivalAtTarget == null) ? 0 : arrivalAtTarget.hashCode());
		result = prime * result + ((departure == null) ? 0 : departure.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FunctionEntry other = (FunctionEntry) obj;
		if (arrivalAtTarget == null) {
			if (other.arrivalAtTarget != null) {
				return false;
			}
		} else if (!arrivalAtTarget.equals(other.arrivalAtTarget)) {
			return false;
		}
		if (departure == null) {
			if (other.departure != null) {
				return false;
			}
		} else if (!departure.equals(other.departure)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ProfileEntry [" + System.lineSeparator() + "departure=" + departure
				+ System.lineSeparator() + "arrivalAtTarget=" + arrivalAtTarget + System.lineSeparator()
				+ "connection=" + connection + "]";
	}

}
