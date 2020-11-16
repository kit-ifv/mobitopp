package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

@ExtendWith(MockitoExtension.class)
public class WeightedHouseholdsTest {

  private static Stream<Setup> scenarios() {
    return new IpuSetupBuilder().buildScenarios();
  }
  
  @ParameterizedTest
  @MethodSource("scenarios")
  public void updateWeightsOnAllHousehold(Setup setup) {
    List<Double> scalingFactors = setup.getScalingFactors();
    double[] expectedWeights = setup.getExpectedWeights();
    WeightedHouseholds weightedHouseholds = setup.getWeightedHouseholds();

    WeightedHouseholds updatedHouseholds = weightedHouseholds.scale();

    List<Double> factors = updatedHouseholds.factors();
    for (int index = 0; index < factors.size(); index++) {
      assertThat(factors.get(index)).isCloseTo(scalingFactors.get(index), Offset.offset(1e-6d));
    }
    assertThat(updatedHouseholds.weights()).isEqualTo(expectedWeights);
    RegionalContext someContext = new DefaultRegionalContext(RegionalLevel.zone,
        setup.getSomeZone().getExternalId());
    RegionalContext otherContext = new DefaultRegionalContext(RegionalLevel.zone,
        setup.getOtherZone().getExternalId());
    RegionalContext anotherContext = new DefaultRegionalContext(RegionalLevel.zone,
        setup.getAnotherZone().getExternalId());
    assertThat(updatedHouseholds.toList())
        .contains(new WeightedHousehold(expectedWeights[0], someContext, setup.getSomePanelHousehold()))
        .contains(new WeightedHousehold(expectedWeights[1], someContext, setup.getOtherPanelHousehold()))
        .contains(new WeightedHousehold(expectedWeights[2], otherContext, setup.getSomePanelHousehold()))
        .contains(new WeightedHousehold(expectedWeights[3], otherContext, setup.getOtherPanelHousehold()))
        .contains(new WeightedHousehold(expectedWeights[4], anotherContext, setup.getSomePanelHousehold()))
        .contains(new WeightedHousehold(expectedWeights[5], anotherContext, setup.getOtherPanelHousehold()));
  }
  
  @ParameterizedTest
  @MethodSource("scenarios")
  void calculateGoodnessOfFit(Setup setup) throws Exception {
    WeightedHouseholds households = setup.getWeightedHouseholds();

    double goodnessOfFit = households.calculateGoodnessOfFit();

    assertThat(goodnessOfFit).isEqualTo(setup.getInitialGoodnessOfFit(), Offset.offset(1e-6d));

    households.scale();
    double updatedGoodness = households.calculateGoodnessOfFit();

    assertThat(updatedGoodness).isEqualTo(setup.getScaledGoodnessOfFit(), Offset.offset(1e-6d));
  }

  @ParameterizedTest
  @MethodSource("scenarios")
  void preservesWeightAfterCopy(Setup setup) throws Exception {
    WeightedHouseholds copied = new WeightedHouseholds(setup.getWeightedHouseholds());
    WeightedHouseholds cloned = setup.getWeightedHouseholds().clone();
    double originalWeight = setup.getWeightedHouseholds().weights()[0];

    setup.getWeightedHouseholds().weights()[0] = originalWeight - 1;

    assertThat(copied.weights()[0]).isCloseTo(originalWeight, Offset.offset(1e-6d));
    assertThat(cloned.weights()[0]).isCloseTo(originalWeight, Offset.offset(1e-6d));
  }

  @Test
  void failsForRequestedWeightOfZero() throws Exception {
    Setup setup = new IpuSetupBuilder().createZeroWeightSetupBuilder().build();
    WeightedHouseholds weightedHouseholds = setup.getWeightedHouseholds();
    weightedHouseholds.scale();
    
    assertThat(weightedHouseholds.weights()).doesNotContain(Double.NaN);
  }
  
}
