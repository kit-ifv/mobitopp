package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public class CommunityOdPairCreator implements OdPairSelector {

	private final CommunityRepository communityRepository;

	public CommunityOdPairCreator(final CommunityRepository communityRepository) {
		super();
		this.communityRepository = communityRepository;
	}

	@Override
	public Collection<OdPair> select(final PersonBuilder person) {
		return getCommutingCommunitiesFor(person)
				.flatMap(Community::zones)
				.map(DemandZone::zone)
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
