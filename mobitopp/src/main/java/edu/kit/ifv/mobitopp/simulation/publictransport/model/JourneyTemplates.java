package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteDirection;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;

public class JourneyTemplates {

	private final Map<TimeProfileKey, JourneyTemplate> profiles;

	public JourneyTemplates() {
		super();
		profiles = new HashMap<>();
	}

	public void add(
			VisumPtLineRouteDirection direction, String name, String lineRouteName, String lineName,
			JourneyTemplate timeProfile) {
		TimeProfileKey key = new TimeProfileKey(direction, name, lineRouteName, lineName);
		profiles.put(key, timeProfile);
	}

	JourneyTemplate from(VisumPtVehicleJourney visum) {
		TimeProfileKey key = new TimeProfileKey(visum.route.direction, visum.timeProfile.name, visum.route.name, visum.route.line.name);
		if (profiles.containsKey(key)) {
			return profiles.get(key);
		}

		throw new IllegalArgumentException("Time profile not found: " + key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((profiles == null) ? 0 : profiles.hashCode());
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
		JourneyTemplates other = (JourneyTemplates) obj;
		if (profiles == null) {
			if (other.profiles != null) {
				return false;
			}
		} else if (!profiles.equals(other.profiles)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TimeProfiles [profiles=" + profiles + "]";
	}

}
