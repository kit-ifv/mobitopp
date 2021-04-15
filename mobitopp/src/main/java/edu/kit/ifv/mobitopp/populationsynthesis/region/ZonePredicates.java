package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;

public abstract class ZonePredicates {

	public static Predicate<DemandRegion> generatesAllZones() {
		return region -> region.zones().allMatch(DemandZone::shouldGeneratePopulation);
	}
	
	public static Predicate<DemandRegion> generatesAnyZone() {
		return region -> region.zones().anyMatch(DemandZone::shouldGeneratePopulation);
	}

}
