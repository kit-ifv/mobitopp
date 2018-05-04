package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteDirection;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;

public class JourneyKey {

	public final String line;
	public final String route;
	public final String direction;
	public final String timeProfile;
	public final int departure;

	public JourneyKey(String line, String route, String direction, String timeProfile, int departure) {
		this.line = line;
		this.route = route;
		this.direction = direction;
		this.timeProfile = timeProfile;
		this.departure = departure;
	}

	public static JourneyKey from(VisumPtVehicleJourney visum) {
		return new JourneyKey(visum.route.line.name, visum.route.name, direction(visum),
				visum.timeProfile.name, visum.departure);
	}

	private static String direction(VisumPtVehicleJourney visum) {
		if (VisumPtLineRouteDirection.H.equals(visum.route.direction)) {
			return ">";
		}
		return "<";
	}

	public JourneyKey derive(int departure) {
		return new JourneyKey(line, route, direction, timeProfile, departure);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + departure;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((timeProfile == null) ? 0 : timeProfile.hashCode());
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
		JourneyKey other = (JourneyKey) obj;
		if (departure != other.departure)
			return false;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (timeProfile == null) {
			if (other.timeProfile != null)
				return false;
		} else if (!timeProfile.equals(other.timeProfile))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JourneyKey [line=" + line + ", route=" + route + ", direction=" + direction
				+ ", timeProfile=" + timeProfile + ", departure=" + departure + "]";
	}

}
