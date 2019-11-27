package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;

@ExtendWith(MockitoExtension.class)
public class DefaultCommunityRepositoryTest {

	private DemandZone someZone;
	private DemandZone otherZone;
	private Community someCommunity;
	private Community otherCommunity;
	private Map<Community, Map<Community, Integer>> commutingRelations;

	@BeforeEach
	public void beforeEach() {
		someZone = ExampleDemandZones.create().someZone();
		otherZone = ExampleDemandZones.create().otherZone();
		someCommunity = new SingleZone(someZone);
		otherCommunity = new SingleZone(otherZone);
		commutingRelations = new LinkedHashMap<>();
	}

	@Test
	void getCommunityContainingZone() throws Exception {
		addRelation(someCommunity, otherCommunity, 1);

		CommunityRepository repository = newRepository();

		assertThat(repository.get(someZone.getId()), is(equalTo(someCommunity)));
	}

	@Test
	void failsForMissingCommunity() throws Exception {
		CommunityRepository repository = newRepository();

		assertThrows(IllegalArgumentException.class, () -> repository.get(someZone.getId()));
	}

	@Test
	void getCommunitiesOnEmptyRepository() throws Exception {
		DefaultCommunityRepository repository = newRepository();

		assertThat(repository.getCommunities(), is(empty()));
	}

	@Test
	void getCommunities() throws Exception {
		addRelation(someCommunity, otherCommunity, 1);
		addRelation(otherCommunity, someCommunity, 0);

		CommunityRepository selector = newRepository();

		assertThat(selector.getCommutingCommunitiesFrom(someZone.getId()).collect(toList()),
				contains(otherCommunity));
	}

	@Test
	void removeEmptyCommunity() throws Exception {
		addRelation(someCommunity, otherCommunity, 2);
		addRelation(otherCommunity, someCommunity, 0);

		DefaultCommunityRepository selector = newRepository();
		selector.notifyAssignedRelation(someZone.zone(), otherZone.zone());
		assertThat(selector.getCommutingCommunitiesFrom(someZone.getId()).collect(toList()),
				contains(otherCommunity));

		selector.notifyAssignedRelation(someZone.zone(), otherZone.zone());
		assertThat(selector.getCommutingCommunitiesFrom(someZone.getId()).collect(toList()),
				is(empty()));
	}

	@Test
	void scaleCommuterRelations() throws Exception {
		addRelation(someCommunity, otherCommunity, 2);
		addRelation(someCommunity, someCommunity, 4);
		addRelation(otherCommunity, someCommunity, 0);

		DefaultCommunityRepository repository = newRepository();
		repository.scale(someCommunity, 3);
		repository.notifyAssignedRelation(someZone.zone(), otherZone.zone());

		List<Community> remaining = repository
				.getCommutingCommunitiesFrom(someZone.getId())
				.collect(toList());

		assertThat(remaining, contains(someCommunity));
	}

	@Test
	void scaleFractionsOfCommuterRelations() throws Exception {
		addRelation(someCommunity, otherCommunity, 1);
		addRelation(someCommunity, someCommunity, 1);
		addRelation(otherCommunity, someCommunity, 0);

		DefaultCommunityRepository repository = newRepository();
		repository.scale(someCommunity, 1);
		repository.notifyAssignedRelation(someZone.zone(), someZone.zone());

		List<Community> remaining = repository
				.getCommutingCommunitiesFrom(someZone.getId())
				.collect(toList());
		
		assertThat(remaining, is(empty()));
	}

	@Test
	void scaleOneMissingOfCommuterRelations() throws Exception {
		addRelation(someCommunity, otherCommunity, 4);
		addRelation(someCommunity, someCommunity, 3);

		DefaultCommunityRepository repository = newRepository();
		repository.scale(someCommunity, 2);

		assertAll(
				() -> assertThat(commutingRelations.get(someCommunity).get(otherCommunity))
						.as("some to other")
						.isOne(),
				() -> assertThat(commutingRelations.get(someCommunity).get(someCommunity))
						.as("some to some")
						.isOne()
				);
	}

	private void addRelation(Community origin, Community destination, int relation) {
		if (!commutingRelations.containsKey(origin)) {
			commutingRelations.put(origin, new LinkedHashMap<>());
		}
		commutingRelations.get(origin).put(destination, relation);
	}

	private DefaultCommunityRepository newRepository() {
		return new DefaultCommunityRepository(commutingRelations);
	}
}
