package edu.kit.ifv.mobitopp.populationsynthesis.community;

import edu.kit.ifv.mobitopp.data.Zone;

public interface CommunitySelector {

	void notifyAssignedRelation(Zone homeZone, Zone destination);

}
