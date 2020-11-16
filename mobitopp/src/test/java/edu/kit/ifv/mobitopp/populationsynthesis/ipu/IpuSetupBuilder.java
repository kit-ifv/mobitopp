package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class IpuSetupBuilder {
  
  public IpuSetupBuilder() {
    super();
  }

  public Stream<Setup> buildScenarios() {
    Setup otherSetup = createOtherSetupBuilder().build();
    Setup someSetup = createSomeSetupBuilder().build();
    return Stream.of(someSetup, otherSetup);
  }

  public Setup.SetupBuilder createSomeSetupBuilder() {
    HouseholdOfPanelData somePanelHousehold = newPanelHousehold(someId);
    HouseholdOfPanelData otherPanelHousehold = newPanelHousehold(otherId);
    DemandZone someZone = mock(DemandZone.class);
    DemandZone otherZone = mock(DemandZone.class);
    DemandZone anotherZone = mock(DemandZone.class);
    when(someZone.getExternalId()).thenReturn("some zone");
    when(otherZone.getExternalId()).thenReturn("other zone");
    when(anotherZone.getExternalId()).thenReturn("another zone");
    when(someZone.getRegionalContext()).thenReturn(new DefaultRegionalContext(RegionalLevel.zone, "some zone"));
    when(otherZone.getRegionalContext()).thenReturn(new DefaultRegionalContext(RegionalLevel.zone, "other zone"));
    when(anotherZone.getRegionalContext()).thenReturn(new DefaultRegionalContext(RegionalLevel.zone, "another zone"));
    List<DemandZone> someDistrictZones = List.of(someZone, otherZone);
    List<DemandZone> otherDistrictZones = List.of(anotherZone);
    List<DemandZone> allZones = new ArrayList<>(someDistrictZones);
    allZones.addAll(otherDistrictZones);
    List<HouseholdOfPanelData> panelHouseholds = List.of(somePanelHousehold, otherPanelHousehold);
    return new Setup.SetupBuilder()
        .scalingFactors(someExpectedFactors())
        .expectedWeights(someExpectedWeights())
        .initialGoodnessOfFit(0.670138888888889d)
        .scaledGoodnessOfFit(2.55555555555556d)
        .weightedHouseholds(createSomeWeightedHouseholds(panelHouseholds, allZones))
        .someZone(someZone)
        .otherZone(otherZone)
        .anotherZone(anotherZone)
        .somePanelHousehold(somePanelHousehold)
        .otherPanelHousehold(otherPanelHousehold);
  }
  
  public Setup.SetupBuilder createOtherSetupBuilder() {
    HouseholdOfPanelData somePanelHousehold = newPanelHousehold(someId);
    HouseholdOfPanelData otherPanelHousehold = newPanelHousehold(otherId);
    DemandZone someZone = mock(DemandZone.class);
    DemandZone otherZone = mock(DemandZone.class);
    DemandZone anotherZone = mock(DemandZone.class);
    when(someZone.getExternalId()).thenReturn("some zone");
    when(otherZone.getExternalId()).thenReturn("other zone");
    when(anotherZone.getExternalId()).thenReturn("another zone");
    when(someZone.getRegionalContext()).thenReturn(new DefaultRegionalContext(RegionalLevel.zone, "some zone"));
    when(otherZone.getRegionalContext()).thenReturn(new DefaultRegionalContext(RegionalLevel.zone, "other zone"));
    when(anotherZone.getRegionalContext()).thenReturn(new DefaultRegionalContext(RegionalLevel.zone, "another zone"));
    List<DemandZone> someDistrictZones = List.of(someZone, otherZone);
    List<DemandZone> otherDistrictZones = List.of(anotherZone);
    List<DemandZone> allZones = new ArrayList<>(someDistrictZones);
    allZones.addAll(otherDistrictZones);
    List<HouseholdOfPanelData> panelHouseholds = List.of(somePanelHousehold, otherPanelHousehold);
    return new Setup.SetupBuilder()
        .scalingFactors(otherExpectedFactors())
        .expectedWeights(otherExpectedWeights())
        .initialGoodnessOfFit(0.458333333333333d)
        .scaledGoodnessOfFit(0.125d)
        .weightedHouseholds(createOtherWeightedHouseholds(panelHouseholds, allZones))
        .someZone(someZone)
        .otherZone(otherZone)
        .anotherZone(anotherZone)
        .somePanelHousehold(somePanelHousehold)
        .otherPanelHousehold(otherPanelHousehold);
  }
  
  private static final short year = 2020;
  private static final HouseholdOfPanelDataId someId = new HouseholdOfPanelDataId(year, 1);
  private static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(year, 2);

  private HouseholdOfPanelData newPanelHousehold(HouseholdOfPanelDataId id) {
    return new HouseholdOfPanelDataBuilder().withId(id).build();
  }

  private List<Double> otherExpectedFactors() {
    return List
        .of(1.333333333d, 1.333333333d, 1.5d, 0.375d, 1.5d, 1.5d, 0.5d, 1.0d, 1.5d, 2.0d, 1.0d,
            0.75d);
  }

  private double[] otherExpectedWeights() {
    return new double[] { 1.0d, 0.5d, 3.0d, 1.0d, 2.0d, 1.5d };
  }

  private List<Double> someExpectedFactors() {
    return List.of(1.0d, 2.0d, 3.0d, 3.0d, 1.0d, 3.0d, 4.0d, 4.0d, 6.0d, 6.0d, 2.0d, 2.0d);
  }

  private double[] someExpectedWeights() {
    return new double[] { 12.0d, 24.0d, 18.0d, 36.0d, 2.0d, 12.0d };
  }

  private WeightedHouseholds createSomeWeightedHouseholds(
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
    return new WeightedHouseholds(panelHouseholds, weights, requestedWeightsMapping,
        householdValues, allZones);
  }

  private int[][] createSomeHouseholdValues() {
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
  private Map<RegionalLevel, List<RequestedWeights>> createSomeWeightOffsetMapping(
      int numberOfHouseholds) {
    return Map
        .of(RegionalLevel.county, List.of(),
            RegionalLevel.community,
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

  private WeightedHouseholds createOtherWeightedHouseholds(
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
    return new WeightedHouseholds(panelHouseholds, weights, requestedWeightsMapping,
        householdValues, allZones);
  }

  private int[][] createOtherHouseholdValues() {
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
  private Map<RegionalLevel, List<RequestedWeights>> createOtherWeightOffsetMapping(
      int numberOfHouseholds) {
    return Map
        .of(RegionalLevel.county, List.of(),
            RegionalLevel.community,
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

}
