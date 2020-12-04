package edu.kit.ifv.mobitopp.simulation.car;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;


public class CarPosition implements Serializable {

	private static final long serialVersionUID = 1L;

	public final Zone zone;
	public final Location location;

	public CarPosition(
		Zone zone,
		Location location
	) {
		this.zone = zone;
		this.location = location;
	}

	public String coordinates() {
		assert  this.location != null;

		return this.location.coordinates();
	}
	
	public Zone zone() {
		return zone;
	}
	
	public Location location() {
		return location;
	}

}

