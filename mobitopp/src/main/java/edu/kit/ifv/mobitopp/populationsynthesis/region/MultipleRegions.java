package edu.kit.ifv.mobitopp.populationsynthesis.region;

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
public class MultipleRegions implements DemandRegion {

	@Getter
	private final String id;
	private final RegionalLevel level;
	private final Demography nominalDemography;
	private final List<DemandRegion> parts;
	private final Demography actualDemography;

	public MultipleRegions(
			final String id, final RegionalLevel level, final Demography nominalDemography,
			final List<DemandRegion> parts) {
		super();
		this.id = id;
		this.level = level;
		this.nominalDemography = nominalDemography;
		this.parts = parts;
		this.actualDemography = nominalDemography.createEmpty();
	}

	public MultipleRegions(
			final String id, final RegionalLevel level, final Demography nominalDemography,
			final DemandRegion... zones) {
		this(id, level, nominalDemography, List.of(zones));
	}

	@Override
	public String getExternalId() {
		return id;
	}

	@Override
	public RegionalLevel regionalLevel() {
		return level;
	}

	@Override
	public List<DemandRegion> parts() {
		return Collections.unmodifiableList(parts);
	}

	@Override
	public Stream<DemandZone> zones() {
		return parts.stream().flatMap(DemandRegion::zones);
	}

	@Override
	public boolean contains(final ZoneId id) {
		return parts.stream().anyMatch(part -> part.contains(id));
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
