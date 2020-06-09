package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPair;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class DemandRegionOdPairCreator implements DemandRegionOdPairSelector {

	private final DemandRegionRelationsRepository relationsRepository;
	private final Predicate<Zone> isDestination;

	public DemandRegionOdPairCreator(
			final DemandRegionRelationsRepository relationsRepository, final ActivityType activityType) {
		super();
		this.relationsRepository = relationsRepository;
		isDestination = zone -> zone.isDestinationFor(activityType);
	}

	public static DemandRegionOdPairSelector forWork(
			final DemandRegionRelationsRepository relationsRepository) {
		return new DemandRegionOdPairCreator(relationsRepository, ActivityType.WORK);
	}

	@Override
	public Collection<OdPair> select(final PersonBuilder person) {
		return getCommutingCommunitiesFor(person)
				.flatMap(DemandRegion::zones)
				.map(DemandZone::zone)
				.filter(isDestination)
				.map(d -> new OdPair(person.homeZone(), d))
				.collect(toList());
	}

	private Stream<DemandRegion> getCommutingCommunitiesFor(PersonBuilder person) {
		return relationsRepository.getCommutingRegionsFrom(person.homeZone().getId());
	}

	@Override
	public void scale(DemandRegion region, int numberOfCommuters) {
		relationsRepository.scale(region, numberOfCommuters);
	}

}
