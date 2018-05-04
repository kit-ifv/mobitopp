package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;

interface UsedJourneys {

	boolean used(Journey journey);

	void use(Journey journey);

}
