package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import lombok.Builder;

@ExtendWith(MockitoExtension.class)
public class WeightedHouseholdsTest {

  @Builder
  public static final class Setup {

    private final DemandZone someZone;
    private final DemandZone otherZone;
    private final DemandZone anotherZone;
    private final HouseholdOfPanelData somePanelHousehold;
    private final HouseholdOfPanelData otherPanelHousehold;

    private final ArrayWeightedHouseholds weightedHouseholds;
    private final List<Double> scalingFactors;
    private final double[] expectedWeights;
    private final double initialGoodnessOfFit;
    private final double scaledGoodnessOfFit;

  }

  private static final short year = 2020;
  private static final HouseholdOfPanelDataId someId = new HouseholdOfPanelDataId(year, 1);
  private static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(year, 2);

  private static HouseholdOfPanelData newPanelHousehold(HouseholdOfPanelDataId id) {
    return new HouseholdOfPanelDataBuilder().withId(id).build();
  }

  private static Stream<Setup> scenarios() {
    HouseholdOfPanelData somePanelHousehold = newPanelHousehold(someId);
    HouseholdOfPanelData otherPanelHousehold = newPanelHousehold(otherId);
    DemandZone someZone = mock(DemandZone.class);
    DemandZone otherZone = mock(DemandZone.class);
    DemandZone anotherZone = mock(DemandZone.class);
    when(someZone.getExternalId()).thenReturn("some zone");
    when(otherZone.getExternalId()).thenReturn("other zone");
    when(anotherZone.getExternalId()).thenReturn("another zone");
    List<DemandZone> someDistrictZones = List.of(someZone, otherZone);
    List<DemandZone> otherDistrictZones = List.of(anotherZone);
    List<DemandZone> allZones = new ArrayList<>(someDistrictZones);
    allZones.addAll(otherDistrictZones);
    List<HouseholdOfPanelData> panelHouseholds = List.of(somePanelHousehold, otherPanelHousehold);
    Setup someSetup = new Setup.SetupBuilder()
        .scalingFactors(someExpectedFactors())
        .expectedWeights(someExpectedWeights())
        .initialGoodnessOfFit(0.670138888888889d)
        .scaledGoodnessOfFit(2.55555555555556d)
        .weightedHouseholds(createSomeWeightedHouseholds(panelHouseholds, allZones))
        .someZone(someZone)
        .otherZone(otherZone)
        .anotherZone(anotherZone)
        .somePanelHousehold(somePanelHousehold)
        .otherPanelHousehold(otherPanelHousehold)
        .build();
    Setup otherSetup = new Setup.SetupBuilder()
        .scalingFactors(otherExpectedFactors())
        .expectedWeights(otherExpectedWeights())
        .initialGoodnessOfFit(0.458333333333333d)
        .scaledGoodnessOfFit(0.125d)
        .weightedHouseholds(createOtherWeightedHouseholds(panelHouseholds, allZones))
        .someZone(someZone)
        .otherZone(otherZone)
        .anotherZone(anotherZone)
        .somePanelHousehold(somePanelHousehold)
        .otherPanelHousehold(otherPanelHousehold)
        .build();
    return Stream.of(someSetup, otherSetup);
  }

  private static List<Double> otherExpectedFactors() {
    return List
        .of(1.333333333d, 1.333333333d, 1.5d, 0.375d, 1.5d, 1.5d, 0.5d, 1.0d, 1.5d, 2.0d, 1.0d,
            0.75d);
  }

  private static double[] otherExpectedWeights() {
    return new double[] { 1.0d, 0.5d, 3.0d, 1.0d, 2.0d, 1.5d };
  }

  private static List<Double> someExpectedFactors() {
    return List.of(1.0d, 2.0d, 3.0d, 3.0d, 1.0d, 3.0d, 4.0d, 4.0d, 6.0d, 6.0d, 2.0d, 2.0d);
  }

  private static double[] someExpectedWeights() {
    return new double[] { 12.0d, 24.0d, 18.0d, 36.0d, 2.0d, 12.0d };
  }

  private static ArrayWeightedHouseholds createSomeWeightedHouseholds(
      List<HouseholdOfPanelData> panelHouseholds, List<DemandZone> allZones) {
    int numberOfHouseholds = panelHouseholds.size();
    int numberOfWeights = numberOfHouseholds * allZones.size();
    double[] weights = new double[numberOfWeights];
    for (int index = 0; index < weights.length; index++) {
      weights[index] = 1.0d;
    }

    Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping = createSomeWeightOffsetMapping(
        numberOfHouseholds);
    int[][] householdValues = createSomeHouseholdValues();
    return new ArrayWeightedHouseholds(panelHouseholds, weights, requestedWeightsMapping,
        householdValues, allZones);
  }

  private static int[][] createSomeHouseholdValues() {
    return new int[][] { { 1, 0, 1, 0, 1, 0 }, { 0, 1, 0, 1, 0, 1 } };
  }

  /**
   * <pre>
      hs1 hs2 ag1 ag2 in1 in2
  h1  h1  1   0   1   0   1   0
  h2  h2  0   1   0   1   0   1
  h1  h3  1   0   1   0   1   0
  h2  h4  0   1   0   1   0   1
  h1  h5  1   0   1   0   1   0
  h2  h6  0   1   0   1   0   1
          3   6   6  12  12  24
                         18  36
                  1   6   2  12
   * </pre>
   **/
  private static Map<RegionalLevel, List<RequestedWeights>> createSomeWeightOffsetMapping(
      int numberOfHouseholds) {
    return Map
        .of(RegionalLevel.community,
            List.of(new RequestedWeights(new int[] { 3, 6 }, 0, 0, 3 * numberOfHouseholds)),
            RegionalLevel.district,
            List
                .of(new RequestedWeights(new int[] { 6, 12 }, 2, 0, 2 * numberOfHouseholds),
                    new RequestedWeights(
                        new int[] { 1, 6 }, 2, 2 * numberOfHouseholds, numberOfHouseholds)),
            RegionalLevel.zone,
            List
                .of(new RequestedWeights(new int[] { 12, 24 }, 4, 0, numberOfHouseholds),
                    new RequestedWeights(new int[] { 18, 36 }, 4, numberOfHouseholds,
                        numberOfHouseholds),
                    new RequestedWeights(new int[] { 2, 12 }, 4, 2 * numberOfHouseholds,
                        numberOfHouseholds)));
  }

  private static ArrayWeightedHouseholds createOtherWeightedHouseholds(
      List<HouseholdOfPanelData> panelHouseholds, List<DemandZone> allZones) {
    int numberOfHouseholds = panelHouseholds.size();
    int numberOfWeights = numberOfHouseholds * allZones.size();
    double[] weights = new double[numberOfWeights];
    for (int index = 0; index < weights.length; index++) {
      weights[index] = 1.0d;
    }

    Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping = createOtherWeightOffsetMapping(
        numberOfHouseholds);
    int[][] householdValues = createOtherHouseholdValues();
    return new ArrayWeightedHouseholds(panelHouseholds, weights, requestedWeightsMapping,
        householdValues, allZones);
  }

  private static int[][] createOtherHouseholdValues() {
    return new int[][] { { 1, 0, 1, 0, 1, 0 }, { 0, 1, 0, 2, 0, 2 } };
  }

  /**
   * <pre>
        hs1 hs2 ag1 ag2 in1 in2
  h1  h1   1   0   1   0   1   0
  h2  h2   0   1   0   2   0   2
  h1  h3   1   0   1   0   1   0
  h2  h4   0   1   0   2   0   2
  h1  h5   1   0   1   0   1   0
  h2  h6   0   1   0   2   0   2
           4   4   4   2   1   1
                           3   2
                   2   4   2   3
   * </pre>
   **/
  private static Map<RegionalLevel, List<RequestedWeights>> createOtherWeightOffsetMapping(
      int numberOfHouseholds) {
    return Map
        .of(RegionalLevel.community,
            List.of(new RequestedWeights(new int[] { 4, 4 }, 0, 0, 3 * numberOfHouseholds)),
            RegionalLevel.district,
            List
                .of(new RequestedWeights(new int[] { 4, 2 }, 2, 0, 2 * numberOfHouseholds),
                    new RequestedWeights(
                        new int[] { 2, 4 }, 2, 2 * numberOfHouseholds, numberOfHouseholds)),
            RegionalLevel.zone,
            List
                .of(new RequestedWeights(new int[] { 1, 1 }, 4, 0, numberOfHouseholds),
                    new RequestedWeights(new int[] { 3, 2 }, 4, numberOfHouseholds,
                        numberOfHouseholds),
                    new RequestedWeights(new int[] { 2, 3 }, 4, 2 * numberOfHouseholds,
                        numberOfHouseholds)));
  }

  @ParameterizedTest
  @MethodSource("scenarios")
  public void updateWeightsOnAllHousehold(Setup setup) {
    List<Double> scalingFactors = setup.scalingFactors;
    double[] expectedWeights = setup.expectedWeights;
    ArrayWeightedHouseholds weightedHouseholds = setup.weightedHouseholds;

    ArrayWeightedHouseholds updatedHouseholds = weightedHouseholds.scale();

    assertThat(updatedHouseholds.factors()).hasSize(1);
    List<Double> factors = updatedHouseholds.factors().get(0);
    for (int index = 0; index < factors.size(); index++) {
      assertThat(factors.get(index)).isCloseTo(scalingFactors.get(index), Offset.offset(1e-6d));
    }
    assertThat(updatedHouseholds.weights()).isEqualTo(expectedWeights);
    RegionalContext someContext = new DefaultRegionalContext(RegionalLevel.zone,
        setup.someZone.getExternalId());
    RegionalContext otherContext = new DefaultRegionalContext(RegionalLevel.zone,
        setup.otherZone.getExternalId());
    RegionalContext anotherContext = new DefaultRegionalContext(RegionalLevel.zone,
        setup.anotherZone.getExternalId());
    assertThat(updatedHouseholds.toList())
        .contains(new WeightedHousehold(expectedWeights[0], someContext, setup.somePanelHousehold))
        .contains(new WeightedHousehold(expectedWeights[1], someContext, setup.otherPanelHousehold))
        .contains(new WeightedHousehold(expectedWeights[2], otherContext, setup.somePanelHousehold))
        .contains(new WeightedHousehold(expectedWeights[3], otherContext, setup.otherPanelHousehold))
        .contains(new WeightedHousehold(expectedWeights[4], anotherContext, setup.somePanelHousehold))
        .contains(new WeightedHousehold(expectedWeights[5], anotherContext, setup.otherPanelHousehold));
  }

  @ParameterizedTest
  @MethodSource("scenarios")
  void calculateGoodnessOfFit(Setup setup) throws Exception {
    ArrayWeightedHouseholds households = setup.weightedHouseholds;

    double goodnessOfFit = households.calculateGoodnessOfFit();

    assertThat(goodnessOfFit).isEqualTo(setup.initialGoodnessOfFit, Offset.offset(1e-6d));

    households.scale();
    double updatedGoodness = households.calculateGoodnessOfFit();

    assertThat(updatedGoodness).isEqualTo(setup.scaledGoodnessOfFit, Offset.offset(1e-6d));
  }

  @ParameterizedTest
  @MethodSource("scenarios")
  void preservesWeightAfterCopy(Setup setup) throws Exception {
    ArrayWeightedHouseholds copied = new ArrayWeightedHouseholds(setup.weightedHouseholds);
    ArrayWeightedHouseholds cloned = setup.weightedHouseholds.clone();
    double originalWeight = setup.weightedHouseholds.weights()[0];

    setup.weightedHouseholds.weights()[0] = originalWeight - 1;

    assertThat(copied.weights()[0]).isCloseTo(originalWeight, Offset.offset(1e-6d));
    assertThat(cloned.weights()[0]).isCloseTo(originalWeight, Offset.offset(1e-6d));
  }
}
