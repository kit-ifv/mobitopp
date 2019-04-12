package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;

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
    DoubleSupplier random = mock(DoubleSupplier.class);
    when(random.getAsDouble()).thenReturn(0.25d, 0.75d);
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 2;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds, is(equalTo(households)));
  }

  @Test
  public void selectFirstHousehold() {
    DoubleSupplier random = () -> 0.25d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 1;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds, contains(first));
  }

  @Test
  public void selectSecondHousehold() {
    DoubleSupplier random = () -> 0.75d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);

    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 1;
    List<WeightedHousehold> selectedHouseholds = selector.selectFrom(households, amount);

    assertThat(selectedHouseholds, contains(second));
  }

  @Test(expected=IllegalArgumentException.class)
  public void selectWithoutHouseholds() {
    DoubleSupplier random = () -> 0.75d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);
    
    List<WeightedHousehold> households = emptyList();
    int amount = 1;
    selector.selectFrom(households, amount);
  }
  
  @Test
  public void selectNoHouseholds() {
    DoubleSupplier random = () -> 0.75d;
    ProbabilityBasedSelector selector = new ProbabilityBasedSelector(random);
    
    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 0;
    List<WeightedHousehold> selected = selector.selectFrom(households, amount);
    
    assertThat(selected, is(empty()));
  }

  private List<WeightedHousehold> createWeightedHouseholds() {
    return asList(first, second);
  }
}
