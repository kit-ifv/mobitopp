package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class SingleZone implements Community {

	private final DemandZone zone;

	@Override
	public Collection<DemandZone> getZones() {
		return Collections.singleton(zone);
	}

	@Override
	public Stream<DemandZone> zones() {
		return Stream.of(zone);
	}

	@Override
	public boolean contains(ZoneId id) {
		return zone.getId().equals(id);
	}

}
