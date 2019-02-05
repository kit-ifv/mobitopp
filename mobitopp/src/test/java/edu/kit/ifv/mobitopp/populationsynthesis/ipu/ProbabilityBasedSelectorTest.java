package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ProbabilityBasedSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;

public class ProbabilityBasedSelectorTest {

  private WeightedHousehold first;
  private WeightedHousehold second;

  @Before
  public void initialise() {
    double weight = 0.5d;
    Map<String, Integer> attributes = emptyMap();
    first = new WeightedHousehold(ExampleHouseholdOfPanelData.anId, weight, attributes);
    second = new WeightedHousehold(ExampleHouseholdOfPanelData.otherId, weight, attributes);
  }

  @Test
  public void selectAll() {
    @SuppressWarnings("unchecked")
    Supplier<Double> random = mock(Supplier.class);
    when(random.get()).thenReturn(0.25d, 0.75d);
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 2;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds, is(equalTo(households)));
  }

  @Test
  public void selectFirstHousehold() {
    Supplier<Double> random = () -> 0.25d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 1;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds, contains(first));
  }
  
  @Test
  public void selectSecondHousehold() {
    Supplier<Double> random = () -> 0.75d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random );
    
    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 1;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);
    
    assertThat(selectedHouseholds, contains(second));
  }

  private List<WeightedHousehold> createWeightedHouseholds() {
    return asList(first, second);
  }
}
