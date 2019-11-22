package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.ZoneId;

public interface CommunityRepository {

	Collection<Community> getCommunities();

	Stream<Community> getCommutingCommunitiesFrom(ZoneId zone);

	Community get(ZoneId id);

	void scale(Community otherCommunity, int numberOfCommuters);

}