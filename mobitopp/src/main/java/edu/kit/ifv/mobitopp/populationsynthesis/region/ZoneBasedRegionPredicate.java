package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;

public class ZoneBasedRegionPredicate implements Predicate<DemandRegion> {

	@Override
	public boolean test(DemandRegion region) {
		return region.zones().allMatch(DemandZone::shouldGeneratePopulation);
	}

}
