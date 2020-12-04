package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;

public interface HouseholdLocationSelector {

	public Location selectLocation(Zone zone);

	public Location selectLocation(edu.kit.ifv.mobitopp.data.Zone zone);
}
