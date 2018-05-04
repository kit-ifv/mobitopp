package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

class StopTransfer {

	private Stop stop;
	private Stop neighbour;
	private RelativeTime walkTime;

	StopTransfer(Stop stop, Stop neighbour, RelativeTime walkTime) {
		super();
		this.stop = stop;
		this.neighbour = neighbour;
		this.walkTime = walkTime;
	}

	Stop stop() {
		return stop;
	}

	Stop neighbour() {
		return neighbour;
	}

	RelativeTime walkTime() {
		return walkTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((neighbour == null) ? 0 : neighbour.hashCode());
		result = prime * result + ((stop == null) ? 0 : stop.hashCode());
		result = prime * result + ((walkTime == null) ? 0 : walkTime.hashCode());
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
		StopTransfer other = (StopTransfer) obj;
		if (neighbour == null) {
			if (other.neighbour != null)
				return false;
		} else if (!neighbour.equals(other.neighbour))
			return false;
		if (stop == null) {
			if (other.stop != null)
				return false;
		} else if (!stop.equals(other.stop))
			return false;
		if (walkTime == null) {
			if (other.walkTime != null)
				return false;
		} else if (!walkTime.equals(other.walkTime))
			return false;
		return true;
	}

}
