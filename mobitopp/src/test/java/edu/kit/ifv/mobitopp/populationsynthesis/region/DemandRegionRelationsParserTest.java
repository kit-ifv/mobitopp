package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionRepository;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class DemandRegionRelationsParserTest {

	private static final String someId = "1";
	private static final String otherId = "2";
	private static BiPredicate<DemandRegion, DemandRegion> acceptAll = (a, b) -> true;

	@Mock
	private DemandRegion someRegion;
	@Mock
	private DemandRegion otherRegion;
	@Mock
	private DemandRegionRepository regions;
	private List<Row> relations;

	@BeforeEach
	public void beforeEach() {
		when(regions.getRegionWith(community, someId)).thenReturn(Optional.of(someRegion));
		when(regions.getRegionWith(community, otherId)).thenReturn(Optional.of(otherRegion));
		relations = new LinkedList<>();
	}

	@Test
	void parseRelations() throws Exception {
		File dummyFile = new File("dummyFile");
		configureRelation(someId, someId, "1");
		configureRelation(someId, otherId, "4");
		configureRelation(otherId, someId, "16");
		configureRelation(otherId, otherId, "64");
		DemandRegionRelationsParser parser = newParser(acceptAll);

		Collection<DemandRegion> regions = parser.parse(dummyFile).getRegions();
		assertThat(regions).containsExactly(someRegion, otherRegion);
	}
	
	@Test
	void rejectOneRelation() throws Exception {
		File dummyFile = new File("dummyFile");
		configureRelation(someId, someId, "1");
		configureRelation(someId, otherId, "4");
		configureRelation(otherId, someId, "16");
		configureRelation(otherId, otherId, "64");
		DemandRegionRelationsParser parser = newParser(reject(someRegion, otherRegion));

		parser.parse(dummyFile);
		Map<DemandRegion, Map<DemandRegion, Integer>> relations = parser.getParsedRelations();
		assertThat(relations.get(someRegion).get(otherRegion)).isZero();
	}

	@SuppressWarnings("unchecked")
	private BiPredicate<DemandRegion, DemandRegion> reject(DemandRegion origin,
		DemandRegion destination) {
		BiPredicate<DemandRegion, DemandRegion> predicate = mock(BiPredicate.class);
		when(predicate.test(any(), any())).thenReturn(true);
		when(predicate.test(origin, destination)).thenReturn(false);
		return predicate;
	}

	private DemandRegionRelationsParser newParser(BiPredicate<DemandRegion, DemandRegion> predicate) {
		return new DemandRegionRelationsParser(community, regions, predicate) {

			Stream<Row> load(File input) {
				return relations.stream();
			}
		};
	}

	private void configureRelation(String origin, String destination, String commuters) {
		this.relations
				.add(Row
						.createRow(asList(origin, destination, commuters),
								asList("origin", "destination", "commuters")));
	}
}
