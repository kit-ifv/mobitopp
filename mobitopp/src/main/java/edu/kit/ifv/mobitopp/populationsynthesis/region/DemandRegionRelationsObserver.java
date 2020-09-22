package edu.kit.ifv.mobitopp.populationsynthesis.region;

import edu.kit.ifv.mobitopp.data.Zone;

public interface DemandRegionRelationsObserver {

	void notifyAssignedRelation(Zone homeZone, Zone destination);

}
