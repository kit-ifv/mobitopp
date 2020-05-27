package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class MultipleZones implements Community {

	@Getter
	private final String id;
	private final Demography nominalDemography;
	private final List<DemandZone> zones;
	private final Demography actualDemography;

	public MultipleZones(String id, Demography nominalDemography, List<DemandZone> zones) {
		super();
		this.id = id;
		this.nominalDemography = nominalDemography;
		this.zones = zones;
		this.actualDemography = nominalDemography.createEmpty();
	}

	public MultipleZones(
			final String id, final Demography nominalDemography, final DemandZone... zones) {
		this(id, nominalDemography, List.of(zones));
	}

	@Override
	public String getExternalId() {
		return id;
	}

	@Override
	public RegionalLevel regionalLevel() {
		return RegionalLevel.community;
	}

	@Override
	public List<DemandRegion> parts() {
		return Collections.unmodifiableList(zones);
	}

	@Override
	public Stream<DemandZone> zones() {
		return zones.stream();
	}

	@Override
	public boolean contains(final ZoneId id) {
		return zones.stream().map(DemandZone::getId).anyMatch(z -> z.equals(id));
	}

	@Override
	public Demography nominalDemography() {
		return nominalDemography;
	}

	@Override
	public Demography actualDemography() {
		return actualDemography;
	}

}
