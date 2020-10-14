package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

@ExtendWith(MockitoExtension.class)
public class SimpleConstraintTest {

  private static final String attributeName = "attribute";
	private static final Offset<Double> margin = Offset.offset(1e-6d);
	private static final short year = 2000;
	private static final HouseholdOfPanelDataId someId = new HouseholdOfPanelDataId(year, 1);
	private static final HouseholdOfPanelDataId anotherId = new HouseholdOfPanelDataId(year, 2);
	private static final double requestedWeight = 6.0d;
  
	@Mock(lenient = true)
	private Attribute attribute;
	
	@BeforeEach
  public void initialise() {
	  when(attribute.name()).thenReturn(attributeName);
  }
	
  @Test
  public void updateWeightsOnAllHousehold() {
    WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
    WeightedHousehold anotherHousehold = newHousehold(anotherId, 2.0d);
    WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
    SimpleConstraint constraint = newConstraint();

    WeightedHouseholds updatedHouseholds = constraint.scaleWeightsOf(households);

    Map<HouseholdOfPanelDataId, Double> idToWeight = updatedHouseholds
        .toList()
        .stream()
        .collect(toMap(WeightedHousehold::id, WeightedHousehold::weight));
    assertThat(idToWeight).containsEntry(someId, 2.0d).containsEntry(anotherId, 4.0d);
	}

	@Test
	public void updateWeightOnSingleHousehold() {
	  String otherAttribute = "other";
		WeightedHousehold someHousehold = newHousehold(someId, 1.0d, otherAttribute);
    WeightedHousehold anotherHousehold = newHousehold(anotherId, 2.0d);
		WeightedHouseholds households = new WeightedHouseholds(asList(anotherHousehold, someHousehold));
		SimpleConstraint constraint = newConstraint();

		WeightedHouseholds updatedHouseholds = constraint.scaleWeightsOf(households);

		Map<HouseholdOfPanelDataId, Double> idToWeight = updatedHouseholds
        .toList()
        .stream()
        .collect(toMap(WeightedHousehold::id, WeightedHousehold::weight));
    assertThat(idToWeight).containsEntry(someId, 1.0d).containsEntry(anotherId, 6.0d);
	}

	@Test
	public void calculatesGoodnessOfFit() {
		WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
		WeightedHousehold anotherHousehold = newHousehold(anotherId, 2.0d);
		WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
		SimpleConstraint constraint = newConstraint();

		double goodnessOfFit = constraint.calculateGoodnessOfFitFor(households);

		assertThat(goodnessOfFit).isCloseTo(0.5d, margin);
	}

	@Test
	public void calculatesAnotherGoodnessOfFit() {
		WeightedHousehold someHousehold = newHousehold(someId, 2.0d);
		WeightedHousehold anotherHousehold = newHousehold(anotherId, 4.0d);
		WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
		SimpleConstraint constraint = newConstraint();

		double goodnessOfFit = constraint.calculateGoodnessOfFitFor(households);

		assertThat(goodnessOfFit).isCloseTo(0.0d, margin);
	}

	@Test
	public void requestsWeightToBeGreaterThanZero() {
		double weight = 0.0d;
		SimpleConstraint constraint = newBaseConstraint(weight);

		SimpleConstraint greaterZero = newBaseConstraint(SimpleConstraint.greaterZero);
		assertThat(constraint).isEqualTo(greaterZero);
	}

	private SimpleConstraint newBaseConstraint(double weight) {
		return new SimpleConstraint(attribute, weight);
	}

	private WeightedHousehold newHousehold(HouseholdOfPanelDataId id, double weight) {
	  return newHousehold(id, weight, attributeName);
	}
	
	private WeightedHousehold newHousehold(HouseholdOfPanelDataId id, double weight, String attribute) {
	  HouseholdOfPanelData panelHousehold = mock(HouseholdOfPanelData.class);
    when(panelHousehold.getId()).thenReturn(id);
	  return new WeightedHousehold(panelHousehold.getId(), weight, attributes(attribute),
				new DefaultRegionalContext(RegionalLevel.community, "1"), panelHousehold);
	}

	private Map<String, Integer> attributes(String attribute) {
		return Map.of(attribute, 1);
	}

	private SimpleConstraint newConstraint() {
		return new SimpleConstraint(attribute, requestedWeight);
	}
}
