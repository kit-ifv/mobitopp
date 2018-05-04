package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import edu.kit.ifv.mobitopp.simulation.Location;

import edu.kit.ifv.mobitopp.network.Zone;

public interface HouseholdLocationSelector {

	public Location selectLocation(Zone zone);

	public Location selectLocation(edu.kit.ifv.mobitopp.data.Zone zone);
}
