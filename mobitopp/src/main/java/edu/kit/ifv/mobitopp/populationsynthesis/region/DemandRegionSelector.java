package edu.kit.ifv.mobitopp.populationsynthesis.region;

import edu.kit.ifv.mobitopp.data.Zone;

public interface DemandRegionSelector {

	void notifyAssignedRelation(Zone homeZone, Zone destination);

}
