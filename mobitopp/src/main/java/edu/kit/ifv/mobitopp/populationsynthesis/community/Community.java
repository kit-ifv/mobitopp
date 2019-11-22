package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;

public interface Community {

	Collection<DemandZone> getZones();

	Stream<DemandZone> zones();

	boolean contains(ZoneId id);

}
