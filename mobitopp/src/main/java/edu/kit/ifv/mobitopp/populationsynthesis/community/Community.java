package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;

public interface Community {

	String getId();

	Collection<DemandZone> getZones();

	Stream<DemandZone> zones();

	boolean contains(ZoneId id);

	Demography nominalDemography();

}
