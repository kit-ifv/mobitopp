package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.Collection;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.community.CommunitySelector;

public interface DemandRegionRelationsRepository extends CommunitySelector {

	Collection<DemandRegion> getRegions();

	Stream<DemandRegion> getCommutingRegionsFrom(ZoneId id);

	void scale(DemandRegion region, int numberOfCommuters);

}
