package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class WeightedHouseholdsTest {

	private static final short year = 2020;
	private static final HouseholdOfPanelDataId someId = new HouseholdOfPanelDataId(year, 1);
	private static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(year, 2);

	@Test
	void copyContent() throws Exception {
		WeightedHousehold someHousehold = newHousehold(someId);
		WeightedHousehold otherHousehold = newHousehold(otherId);
		WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, otherHousehold));
		
		WeightedHouseholds copy = households.deepCopy();
		
		assertThat(households).isEqualTo(copy);
		assertThat(households).isNotSameAs(copy);
		assertAll(households
				.toList()
				.stream()
				.map(
						household -> () -> assertThat(copy.toList()).noneMatch(copied -> copied == household)));
	}

	private WeightedHousehold newHousehold(HouseholdOfPanelDataId id) {
		return new WeightedHousehold(id, 1.0d, attributes(),
				new DefaultRegionalContext(RegionalLevel.community, "1"));
	}

	private Map<String, Integer> attributes() {
		return emptyMap();
	}
}
