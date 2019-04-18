package edu.kit.ifv.mobitopp.simulation.opportunities;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;

public class Opportunity {

	private final Zone zone;
	private final ActivityType activityType;
	private final Location location;
	private final Integer attractivity;

	public Opportunity(
			Zone zone, ActivityType activityType, Location location, Integer attractivity) {
		super();
		this.zone = zone;
		this.activityType = activityType;
		this.location = location;
		this.attractivity = attractivity;
	}

	public Zone zone() {
		return zone;
	}

	public ActivityType activityType() {
		return activityType;
	}

	public Location location() {
		return location;
	}

	public Integer attractivity() {
		return attractivity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
		result = prime * result + ((attractivity == null) ? 0 : attractivity.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.getInternalId().hashCode());
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
		Opportunity other = (Opportunity) obj;
		if (activityType != other.activityType)
			return false;
		if (attractivity == null) {
			if (other.attractivity != null)
				return false;
		} else if (!attractivity.equals(other.attractivity))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (zone == null) {
			if (other.zone != null)
				return false;
		} else if (zone.getInternalId() != other.zone.getInternalId())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Opportunity [zone=" + zone + ", activityType=" + activityType + ", location=" + location
				+ ", attractivity=" + attractivity + "]";
	}

}
