package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class StationPath {

	private final Station station;
	private final RelativeTime duration;

	public StationPath(Station station, RelativeTime duration) {
		super();
		this.station = station;
		this.duration = duration;
	}

	Collection<StopPath> pathsToStops() {
		ArrayList<StopPath> stops = new ArrayList<>();
		station.forEach(stop -> stops.add(distanceTo(stop)));
		return stops;
	}

	private StopPath distanceTo(Stop stop) {
		return new StopPath(stop, duration);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((station == null) ? 0 : station.hashCode());
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
		StationPath other = (StationPath) obj;
		if (duration == null) {
			if (other.duration != null) {
				return false;
			}
		} else if (!duration.equals(other.duration)) {
			return false;
		}
		if (station == null) {
			if (other.station != null) {
				return false;
			}
		} else if (!station.equals(other.station)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PathToStation [station=" + station + ", duration=" + duration + "]";
	}

}
