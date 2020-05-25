package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;

public interface Community extends DemandRegion {

	String getId();

	List<DemandZone> getZones();

	Stream<DemandZone> zones();

	boolean contains(ZoneId id);

	Demography nominalDemography();

}
