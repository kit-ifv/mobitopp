package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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

	@Mock
	private DemandRegion someRegion;
	@Mock
	private DemandRegion otherRegion;
	@Mock
	private DemandRegionRepository regions;
	private List<Row> relations;

	@BeforeEach
	public void beforeEach() {
		when(regions.getRegionWith(community, someId)).thenReturn(someRegion);
		when(regions.getRegionWith(community, otherId)).thenReturn(otherRegion);
		relations = new LinkedList<>();
	}

	@Test
	void parseRelations() throws Exception {
		File dummyFile = new File("dummyFile");
		configureRelation(someId, someId, "1");
		configureRelation(someId, otherId, "4");
		configureRelation(otherId, someId, "16");
		configureRelation(otherId, otherId, "64");
		DemandRegionRelationsParser parser = newParser();

		Collection<DemandRegion> regions = parser.parse(dummyFile).getRegions();
		assertThat(regions).containsExactly(someRegion, otherRegion);
	}

	private DemandRegionRelationsParser newParser() {
		return new DemandRegionRelationsParser(community, regions) {

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
