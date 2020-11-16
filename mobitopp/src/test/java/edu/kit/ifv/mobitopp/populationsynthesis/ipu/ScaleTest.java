package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ScaleTest {

  @Test
  void ensureWeightsAreAlwaysAllowed() throws Exception {
    double[] weights = new double[] { 1.0d, 2.0d };
    int[][] householdValues = new int[][] { { 1, 2 }, { 3, 4 } };
    Scale scale = new Scale(weights, householdValues);
    
    scale.process(0, 2, 0, 0, 0);
    
    assertThat(weights).doesNotContain(Double.NaN);
  }
}
