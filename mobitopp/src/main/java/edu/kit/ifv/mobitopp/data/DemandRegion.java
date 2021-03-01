package edu.kit.ifv.mobitopp.data;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;

public interface DemandRegion {

	String getExternalId();

	RegionalLevel regionalLevel();

	default RegionalContext getRegionalContext() {
		return new DefaultRegionalContext(regionalLevel(), getExternalId());
	}

	/**
	 * Return all siblings of the {@link DemandRegion}. For nodes, all sublings are
	 * returned. For leafs and empty {@link List} will be returned.
	 * 
	 * @return all siblings of this {@link DemandRegion}
	 */
	List<DemandRegion> parts();

	/**
	 * Return all zones of the {@link DemandRegion}. For hierarchical
	 * {@link DemandRegion}s, all zones within the hierarchy are returned as
	 * {@link Stream}.
	 * 
	 * @return all zones within this {@link DemandRegion}
	 */
	Stream<DemandZone> zones();

	boolean contains(ZoneId id);

	Demography nominalDemography();

	Demography actualDemography();

}