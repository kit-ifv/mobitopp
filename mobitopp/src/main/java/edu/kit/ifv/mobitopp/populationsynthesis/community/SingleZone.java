package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import lombok.ToString;

@ToString
public class SingleZone implements Community {

	private final DemandZone zone;
	private final Demography nominalDemography;
	
	public SingleZone(DemandZone zone) {
		this.zone = zone;
		this.nominalDemography = zone.nominalDemography();
	}

	@Override
	public String getId() {
		return zone.getId().getExternalId();
	}
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
	
	@Override
	public Demography nominalDemography() {
		return nominalDemography;
	}

}
