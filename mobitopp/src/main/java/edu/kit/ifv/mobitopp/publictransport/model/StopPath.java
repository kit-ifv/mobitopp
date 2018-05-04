package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class StopPath {

	private final Stop stop;
	private final RelativeTime duration;

	public StopPath(Stop stop, RelativeTime duration) {
		super();
		this.stop = stop;
		this.duration = duration;
	}

	public Stop stop() {
		return stop;
	}

	public RelativeTime duration() {
		return duration;
	}

	public Time arrivalTimeStartingAt(Time currentTime) {
		return currentTime.plus(duration);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((stop == null) ? 0 : stop.hashCode());
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
		StopPath other = (StopPath) obj;
		if (duration == null) {
			if (other.duration != null) {
				return false;
			}
		} else if (!duration.equals(other.duration)) {
			return false;
		}
		if (stop == null) {
			if (other.stop != null) {
				return false;
			}
		} else if (!stop.equals(other.stop)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "StopDistance [stop=" + stop + ", duration=" + duration + "]";
	}

}
