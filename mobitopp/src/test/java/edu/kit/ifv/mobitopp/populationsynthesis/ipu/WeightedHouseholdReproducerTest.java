package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

@ExtendWith(MockitoExtension.class)
public class WeightedHouseholdReproducerTest {

	private static final RegionalContext context = new DefaultRegionalContext(RegionalLevel.zone,
			"1");

	@Mock
	private Attribute someAttribute;
	@Mock
	private Attribute otherAttribute;
	@Mock
	private WeightedHouseholdSelector householdSelector;

	@Test
	void multipliesHouseholdsByWeight() throws Exception {
		String someAttributeName = "attribute-name";
		when(someAttribute.name()).thenReturn(someAttributeName);
		Map<String, Integer> attributes = Map.of(someAttributeName, 1);
		int amount = 3;
		double weight = amount / 2.0d;
		WeightedHousehold someHousehold = createHousehold(attributes, weight);
		WeightedHousehold otherHousehold = createHousehold(attributes, weight);
		List<WeightedHousehold> selectedHouseholds = List
				.of(someHousehold, otherHousehold, someHousehold);
		List<WeightedHousehold> households = List.of(someHousehold, otherHousehold);
		when(householdSelector.selectFrom(households, amount)).thenReturn(selectedHouseholds);
		List<Attribute> householdAttributes = List.of(someAttribute);

		WeightedHouseholdReproducer reproducer = new WeightedHouseholdReproducer(householdAttributes,
				householdSelector);
		Stream<WeightedHousehold> moreHouseholds = reproducer.getHouseholdsToCreate(households);

		assertThat(moreHouseholds).containsExactlyElementsOf(selectedHouseholds);
		verify(householdSelector).selectFrom(households, amount);
	}

	@Test
	void multipliesHouseholdsPerAttributeByWeight() throws Exception {
		Map<String, Integer> someAttributes = createSomeAttributes();
		Map<String, Integer> otherAttributes = createOtherAttributes();
		int amount = 3;
		double weight = amount / 2.0d;
		WeightedHousehold someHousehold = createHousehold(someAttributes, weight);
		WeightedHousehold ansomeHousehold = createHousehold(someAttributes, weight);
		WeightedHousehold otherHousehold = createHousehold(otherAttributes, weight);
		WeightedHousehold anotherHousehold = createHousehold(otherAttributes, weight);
		List<WeightedHousehold> someHouseholds = List.of(someHousehold, ansomeHousehold);
		List<WeightedHousehold> otherHouseholds = List.of(otherHousehold, anotherHousehold);
		List<WeightedHousehold> someSelectedHouseholds = List
				.of(someHousehold, ansomeHousehold, someHousehold);
		List<WeightedHousehold> otherSelectedHouseholds = List
				.of(anotherHousehold, otherHousehold, anotherHousehold);
		when(householdSelector.selectFrom(someHouseholds, amount)).thenReturn(someSelectedHouseholds);
		when(householdSelector.selectFrom(otherHouseholds, amount)).thenReturn(otherSelectedHouseholds);
		List<Attribute> householdAttributes = List.of(someAttribute, otherAttribute);
		List<WeightedHousehold> allHouseholds = List.of(someHousehold, ansomeHousehold, otherHousehold, anotherHousehold);

		WeightedHouseholdReproducer reproducer = new WeightedHouseholdReproducer(householdAttributes,
				householdSelector);
		List<WeightedHousehold> moreHouseholds = reproducer
				.getHouseholdsToCreate(allHouseholds)
				.collect(toList());

		assertThat(moreHouseholds)
				.containsAll(someSelectedHouseholds)
				.containsAll(otherSelectedHouseholds);
		verify(householdSelector).selectFrom(someHouseholds, amount);
	}

	private WeightedHousehold createHousehold(Map<String, Integer> attributes, double weight) {
		short year = 2000;
		long number = 1;
		HouseholdOfPanelDataId id = new HouseholdOfPanelDataId(year, number);
		return new WeightedHousehold(id, weight, attributes, context);
	}

	private Map<String, Integer> createOtherAttributes() {
		String otherAttributeName = "other-attribute";
		when(otherAttribute.name()).thenReturn(otherAttributeName);
		return Map.of(otherAttributeName, 1);
	}

	private Map<String, Integer> createSomeAttributes() {
		String someAttributeName = "some-attribute";
		when(someAttribute.name()).thenReturn(someAttributeName);
		return Map.of(someAttributeName, 1);
	}
}
