package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandRegion;

public interface DemandRegionRepository {

	List<DemandRegion> getRegionsOf(RegionalLevel level);

	DemandRegion getRegionWith(RegionalLevel level, String id);
}
