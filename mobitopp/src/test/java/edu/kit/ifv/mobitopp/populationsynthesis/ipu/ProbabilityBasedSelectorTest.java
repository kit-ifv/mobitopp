package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

public class ProbabilityBasedSelectorTest {

  private static final RegionalContext context = new DefaultRegionalContext(RegionalLevel.community,
			"1");
	private WeightedHousehold first;
  private WeightedHousehold second;

  @BeforeEach
  public void initialise() {
    double weight = 0.5d;
    Map<String, Integer> attributes = emptyMap();
    first = new WeightedHousehold(ExampleHouseholdOfPanelData.anId, weight, attributes, context,
        ExampleHouseholdOfPanelData.household);
    second = new WeightedHousehold(ExampleHouseholdOfPanelData.otherId, weight, attributes, context,
        ExampleHouseholdOfPanelData.otherHousehold);
  }

  @Test
  public void selectAll() {
    DoubleSupplier random = mock(DoubleSupplier.class);
    when(random.getAsDouble()).thenReturn(0.25d, 0.75d);
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 2;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds).isEqualTo(households);
  }

  @Test
  public void selectFirstHousehold() {
    DoubleSupplier random = () -> 0.25d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 1;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds).contains(first);
  }

  @Test
  public void selectSecondHousehold() {
    DoubleSupplier random = () -> 0.75d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 1;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds).contains(second);
  }

  @Test
  public void selectWithoutHouseholds() {
    DoubleSupplier random = () -> 0.75d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);
    
    List<WeightedHousehold> households = emptyList();
    int amount = 1;
    assertThrows(IllegalArgumentException.class, () -> selector.selectFrom(households, amount));
  }
  
  @Test
  public void selectNoHouseholds() {
    DoubleSupplier random = () -> 0.75d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);
    
    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 0;
    List<WeightedHousehold> selected = selector.selectFrom(households, amount);
    
    assertThat(selected).isEmpty();
  }

	private List<WeightedHousehold> createWeightedHouseholds() {
		return new LinkedList<>(List.of(first, second));
	}
}
