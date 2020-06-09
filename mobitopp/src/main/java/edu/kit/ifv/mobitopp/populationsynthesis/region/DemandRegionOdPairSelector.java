package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.Collection;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public interface DemandRegionOdPairSelector {

	Collection<OdPair> select(PersonBuilder person);

	void scale(DemandRegion region, int numberOfCommuters);
	
}
