package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class CommunityOdPairCreator implements OdPairSelector {

	private final CommunityRepository communityRepository;
	private final Predicate<Zone> isDestination;

	public CommunityOdPairCreator(final CommunityRepository communityRepository, final ActivityType activityType) {
		super();
		this.communityRepository = communityRepository;
		isDestination = zone -> zone.isDestinationFor(activityType);
	}
	
	public static CommunityOdPairCreator forWork(final CommunityRepository communityRepository) {
		return new CommunityOdPairCreator(communityRepository, ActivityType.WORK);
	}

	@Override
	public Collection<OdPair> select(final PersonBuilder person) {
		return getCommutingCommunitiesFor(person)
				.flatMap(Community::zones)
				.map(DemandZone::zone)
				.filter(isDestination)
				.map(d -> new OdPair(person.homeZone(), d))
				.collect(toList());
	}

	private Stream<Community> getCommutingCommunitiesFor(PersonBuilder person) {
		return communityRepository.getCommutingCommunitiesFrom(person.homeZone().getId());
	}

	@Override
	public void scale(Community community, int numberOfCommuters) {
		communityRepository.scale(community, numberOfCommuters);
	}

}
