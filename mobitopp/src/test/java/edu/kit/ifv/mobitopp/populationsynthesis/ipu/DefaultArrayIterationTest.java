package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

public class DefaultArrayIterationTest {

  @Test
  void adjustWeightsOfHouseholds() throws Exception {
    WeightedHouseholds households = mock(WeightedHouseholds.class);
    when(households.scale()).thenReturn(households);

    DefaultArrayIteration iteration = new DefaultArrayIteration();
    WeightedHouseholds updatedHouseholds = iteration.adjustWeightsOf(households);

    assertThat(updatedHouseholds).isSameAs(households);

    verify(households).scale();
  }

  @Test
  void calculateGoodnessOfFit() throws Exception {
    double goodness = 1.0d;
    WeightedHouseholds households = mock(WeightedHouseholds.class);
    when(households.calculateGoodnessOfFit()).thenReturn(goodness);

    DefaultArrayIteration iteration = new DefaultArrayIteration();
    double goodnessOfFit = iteration.calculateGoodnessOfFitFor(households);
    
    assertThat(goodnessOfFit).isEqualTo(goodness, Offset.offset(1e-6d));
    
    verify(households).calculateGoodnessOfFit();
  }
}
