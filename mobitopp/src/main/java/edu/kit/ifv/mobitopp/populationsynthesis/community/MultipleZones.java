package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
@ToString(of = "id")
public class MultipleZones implements Community {

	@Getter
	private final String id;
	private final Demography nominalDemography;
	private final Collection<DemandZone> zones;

	public MultipleZones(
			final String id, final Demography nominalDemography, final DemandZone... zones) {
		this(id, nominalDemography, asList(zones));
	}
	
	@Override
	public Collection<DemandZone> getZones() {
		return Collections.unmodifiableCollection(zones);
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

}
