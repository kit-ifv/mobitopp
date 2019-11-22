package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class CommunityRelationsParserTest {

	private static final String someId = "1";
	private static final String otherId = "2";

	@Mock
	private Community someCommunity;
	@Mock
	private Community otherCommunity;
	private List<Row> relations;
	private Map<String, Community> communities;

	@BeforeEach
	public void beforeEach() {
		communities = new HashMap<>();
		communities.put(someId, someCommunity);
		communities.put(otherId, otherCommunity);
		relations = new LinkedList<>();
	}

	@Test
	void parseRelations() throws Exception {
		File dummyFile = new File("dummyFile");
		configureRelation(someId, someId, "1");
		configureRelation(someId, otherId, "4");
		configureRelation(otherId, someId, "16");
		configureRelation(otherId, otherId, "64");
		CommunityRelationsParser parser = newParser();

		assertThat(parser.parse(dummyFile).getCommunities(), contains(someCommunity, otherCommunity));
	}

	private CommunityRelationsParser newParser() {
		return new CommunityRelationsParser(communities) {

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
