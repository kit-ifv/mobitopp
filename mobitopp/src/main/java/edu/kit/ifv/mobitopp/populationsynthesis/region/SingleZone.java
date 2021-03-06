package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import lombok.ToString;

@ToString
public class SingleZone implements DemandRegion {

	private final DemandZone zone;

	public SingleZone(DemandZone zone) {
		this.zone = zone;
	}

	@Override
	public String getExternalId() {
		return zone.getId().getExternalId();
	}

	@Override
	public RegionalLevel regionalLevel() {
		return RegionalLevel.community;
	}

	@Override
	public List<DemandRegion> parts() {
		return List.of(zone);
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
		return zone.nominalDemography();
	}

	@Override
	public Demography actualDemography() {
		return zone.actualDemography();
	}

}
