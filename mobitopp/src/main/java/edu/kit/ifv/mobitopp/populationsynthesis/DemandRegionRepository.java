package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.DemandRegion;

public interface DemandRegionRepository {

	List<DemandRegion> getRegionsOf(RegionalLevel level);

	Optional<DemandRegion> getRegionWith(RegionalLevel level, String id);
}
