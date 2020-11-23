package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;

public class StandardDemandRegionRelationsRepositoryTest {
  
	private DemandZone someZone;
	private DemandZone otherZone;
	private DemandRegion someRegion;
	private DemandRegion otherRegion;
	private Map<DemandRegion, Map<DemandRegion, Integer>> relations;

	@BeforeEach
	public void beforeEach() {
		someZone = ExampleDemandZones.create().getSomeZone();
		otherZone = ExampleDemandZones.create().getOtherZone();
		someRegion = new SingleZone(someZone);
		otherRegion = new SingleZone(otherZone);
		relations = new LinkedHashMap<>();
	}

	@Test
	void getCommunityContainingZone() throws Exception {
		addRelation(someRegion, otherRegion, 1);

		StandardDemandRegionRelationsRepository repository = newRepository();

		assertThat(repository.get(someZone.getId()), is(equalTo(someRegion)));
	}

	@Test
	void failsForMissingCommunity() throws Exception {
		StandardDemandRegionRelationsRepository repository = newRepository(List.of(otherRegion));

		assertThrows(IllegalArgumentException.class, () -> repository.get(someZone.getId()));
	}
  
  @Test
  void getUnidirectionalRelation() throws Exception {
    addRelation(someZone, otherZone, 1);
    
    StandardDemandRegionRelationsRepository repository = newRepository();
    
    DemandRegion demandRegion = repository.get(otherZone.getId());
    
    assertThat(demandRegion).isEqualTo(otherRegion);
  }

	@Test
	void getCommunitiesOnEmptyRepository() throws Exception {
		DemandRegionRelationsRepository repository = newRepository();

		assertThat(repository.getRegions(), is(empty()));
	}

	@Test
	void getCommunities() throws Exception {
		addRelation(someRegion, otherRegion, 1);
		addRelation(otherRegion, someRegion, 0);

		DemandRegionRelationsRepository selector = newRepository();

		assertThat(selector.getCommutingRegionsFrom(someZone.getId()).collect(toList()),
				contains(otherRegion));
	}

	@Test
	void removeEmptyCommunity() throws Exception {
		addRelation(someRegion, otherRegion, 2);
		addRelation(otherRegion, someRegion, 0);

		DemandRegionRelationsRepository selector = newRepository();
		selector.notifyAssignedRelation(someZone.zone(), otherZone.zone());
		assertThat(selector.getCommutingRegionsFrom(someZone.getId()).collect(toList()),
				contains(otherRegion));

		selector.notifyAssignedRelation(someZone.zone(), otherZone.zone());
		assertThat(selector.getCommutingRegionsFrom(someZone.getId()).collect(toList()),
				is(empty()));
	}

	@Test
	void scaleCommuterRelations() throws Exception {
		addRelation(someRegion, otherRegion, 2);
		addRelation(someRegion, someRegion, 4);
		addRelation(otherRegion, someRegion, 0);

		DemandRegionRelationsRepository repository = newRepository();
		repository.scale(someRegion, 3);
		repository.notifyAssignedRelation(someZone.zone(), otherZone.zone());

		List<DemandRegion> remaining = repository
				.getCommutingRegionsFrom(someZone.getId())
				.collect(toList());

		assertThat(remaining, contains(someRegion));
	}

	@Test
	void scaleFractionsOfCommuterRelations() throws Exception {
		addRelation(someRegion, otherRegion, 1);
		addRelation(someRegion, someRegion, 1);
		addRelation(otherRegion, someRegion, 0);

		DemandRegionRelationsRepository repository = newRepository();
		repository.scale(someRegion, 1);
		repository.notifyAssignedRelation(someZone.zone(), someZone.zone());

		List<DemandRegion> remaining = repository
				.getCommutingRegionsFrom(someZone.getId())
				.collect(toList());
		
		assertThat(remaining, is(empty()));
	}

	@Test
	void scaleOneMissingOfCommuterRelations() throws Exception {
		addRelation(someRegion, otherRegion, 4);
		addRelation(someRegion, someRegion, 3);

		DemandRegionRelationsRepository repository = newRepository();
		repository.scale(someRegion, 2);

		assertAll(
				() -> assertThat(relations.get(someRegion).get(otherRegion))
						.as("some to other")
						.isOne(),
				() -> assertThat(relations.get(someRegion).get(someRegion))
						.as("some to some")
						.isOne()
				);
	}

	private void addRelation(DemandRegion origin, DemandRegion destination, int relation) {
		if (!relations.containsKey(origin)) {
			relations.put(origin, new LinkedHashMap<>());
		}
		relations.get(origin).put(destination, relation);
	}

	private StandardDemandRegionRelationsRepository newRepository() {
    Collection<DemandRegion> regions = List.of(someRegion, otherRegion);
    return newRepository(regions);
	}

  protected StandardDemandRegionRelationsRepository newRepository(
      final Collection<DemandRegion> regions) {
    return new StandardDemandRegionRelationsRepository(relations, regions);
  }
}