package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	  Set<ZoneId> filteredZones = new TreeSet<>();
		List<OdPair> odPairs = getCommutingCommunitiesFor(person)
		    .flatMap(DemandRegion::zones)
				.map(DemandZone::zone)
				.peek(zone -> filteredZones.add(zone.getId()))
				.filter(isDestination)
				.peek(zone -> filteredZones.remove(zone.getId()))
				.map(d -> new OdPair(person.homeZone(), d))
				.collect(toList());
    if (odPairs.isEmpty()) {
    	log.warn(String
              .format("No destinations left for person %s. Filtered out the zones: %s",
                  person.getId(), filteredZones));
    }
    return odPairs;
	}

	private Stream<DemandRegion> getCommutingCommunitiesFor(PersonBuilder person) {
		return relationsRepository.getCommutingRegionsFrom(person.homeZone().getId());
	}

	@Override
	public void scale(DemandRegion region, int numberOfCommuters) {
		relationsRepository.scale(region, numberOfCommuters);
	}

}
