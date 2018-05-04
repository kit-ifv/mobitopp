package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;

public class ZoneProfile {

	private final Zone zone;
	private final Profile profile;

	public ZoneProfile(Zone zone, Profile profile) {
		super();
		this.zone = zone;
		this.profile = profile;
	}

	public Zone zone() {
		return zone;
	}

	public Profile profile() {
		return profile;
	}
}
