package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;

public class ZoneAndLocation {
	
	public final Zone zone;
	public final Location location;
	
	public ZoneAndLocation(Zone zone, Location location) {
		
		assert zone != null;
		assert location != null : "location is null";
		
		this.zone=zone;
		this.location=location;
	}
	
	public Zone zone() {
		return this.zone;
	}
	
	public Location location() {
		return this.location;
	}

	public static ZoneAndLocation atCenterOf(Zone zone) {
	  return new ZoneAndLocation(zone, zone.centroidLocation());
	}
}
