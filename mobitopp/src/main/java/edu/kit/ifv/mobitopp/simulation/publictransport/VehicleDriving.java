package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleDriving {

	private final RelativeTime travelTime;
	private final ConnectionId id;

	public VehicleDriving(ConnectionId id, RelativeTime travelTime) {
		super();
		this.travelTime = travelTime;
		this.id = id;
	}

	public Optional<Time> nextArrival(Optional<Time> nextDeparture) {
		return nextDeparture.map(time -> time.plus(travelTime));
	}

	public ConnectionId nextConnection() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((travelTime == null) ? 0 : travelTime.hashCode());
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
		VehicleDriving other = (VehicleDriving) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (travelTime == null) {
			if (other.travelTime != null)
				return false;
		} else if (!travelTime.equals(other.travelTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [travelTime=" + travelTime + ", id=" + id + "]";
	}

}