package edu.kit.ifv.mobitopp.simulation;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;

public class FixedDestination implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ActivityType activityType;
	private final Zone zone;
	private final Location location;

	public FixedDestination(ActivityType activityType, Zone zone, Location location) {
		super();
		this.activityType = activityType;
		this.zone = zone;
		this.location = location;
	}

	public ActivityType activityType() {
		return activityType;
	}
	
	public Zone zone() {
		return zone;
	}
	
	public Location location() {
		return location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
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
		FixedDestination other = (FixedDestination) obj;
		if (activityType != other.activityType)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (zone == null) {
			if (other.zone != null)
				return false;
		} else if (zone.getOid() != other.zone.getOid())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FixedDestination [activityType=" + activityType + ", zone=" + zone + ", location="
				+ location + "]";
	}


}
