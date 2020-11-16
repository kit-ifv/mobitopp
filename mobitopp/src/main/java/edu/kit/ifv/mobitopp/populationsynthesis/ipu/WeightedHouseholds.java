package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class WeightedHouseholds {

  private static final List<RegionalLevel> orderOfRegionalLevels = createOrderOfRegionalLevels();

  private final List<HouseholdOfPanelData> households;
  private final double[] weights;
  private final Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping;
  private final int[][] householdValues;
  private final List<DemandZone> zones;
  private final List<Double> factors;

  public WeightedHouseholds(
      List<HouseholdOfPanelData> households, double[] weights,
      Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping, int[][] householdValues,
      List<DemandZone> zones) {
    super();
    this.households = households;
    this.weights = weights;
    this.requestedWeightsMapping = requestedWeightsMapping;
    this.householdValues = householdValues;
    this.zones = zones;
    this.factors = new LinkedList<>();
  }

  private static List<RegionalLevel> createOrderOfRegionalLevels() {
    return EnumSet
        .allOf(RegionalLevel.class)
        .stream()
        .sorted(Comparator.comparing(RegionalLevel::ordinal).reversed())
        .collect(Collectors.toList());
  }

  public WeightedHouseholds(WeightedHouseholds other) {
    this(other.households, Arrays.copyOf(other.weights, other.weights.length),
        other.requestedWeightsMapping, other.householdValues, other.zones);
  }

  public WeightedHouseholds clone() {
    return new WeightedHouseholds(this);
  }

  public WeightedHouseholds scale() {
    Scale scale = new Scale(weights, householdValues);
    process(scale);
    factors.addAll(scale.factors());
    return this;
  }

  protected void process(WeightedHouseholdsConsumer processor) {
    for (RegionalLevel level : orderOfRegionalLevels) {
      List<RequestedWeights> requestedWeightsPerRegion = requestedWeightsMapping.get(level);
      for (int partIndex = 0; partIndex < requestedWeightsPerRegion.size(); partIndex++) {
        RequestedWeights requestedWeightsList = requestedWeightsPerRegion.get(partIndex);
        int offset = requestedWeightsList.getWeightOffset();
        int numberOfWeightsPerPart = requestedWeightsList.getNumberOfWeights();
        int[] requestedWeights = requestedWeightsList.getRequestedWeights();
        for (int relativeAttribute = 0; relativeAttribute < requestedWeights.length; relativeAttribute++) {
          int absoluteAttributeIndex = relativeAttribute
              + requestedWeightsList.getAtttributeOffset();
          double requestedWeight = requestedWeights[relativeAttribute];
          if (0 == requestedWeight) {
            continue;
          }
          double totalWeight = totalWeight(absoluteAttributeIndex, offset, numberOfWeightsPerPart);
          processor
              .process(offset, numberOfWeightsPerPart, absoluteAttributeIndex, requestedWeight,
                  totalWeight);
        }
        offset += numberOfWeightsPerPart;
      }
    }
  }

  private double totalWeight(int attributeIndex, int offset, int numberOfWeightsPerPart) {
    double newWeight = 0.0d;
    for (int weightsIndex = offset; (weightsIndex < offset + numberOfWeightsPerPart)
        && (weightsIndex < weights.length); weightsIndex++) {
      int householdIndex = weightsIndex % numberOfHouseholds();
      newWeight += weights[weightsIndex] * householdValues[householdIndex][attributeIndex];
    }
    return newWeight;
  }

  private int numberOfHouseholds() {
    return householdValues.length;
  }

  public List<WeightedHousehold> toList() {
    List<WeightedHousehold> newHouseholds = new LinkedList<>();
    for (int index = 0; index < weights.length; index++) {
      HouseholdOfPanelData panelHousehold = households.get(index % numberOfHouseholds());
      RegionalContext context = zones.get(index / numberOfHouseholds()).getRegionalContext();
      WeightedHousehold newHousehold = new WeightedHousehold(weights[index], context,
          panelHousehold);
      newHouseholds.add(newHousehold);
    }
    return newHouseholds;
  }

  public double calculateGoodnessOfFit() {
    GoodnessOfFit goodnessOfFit = new GoodnessOfFit();
    process(goodnessOfFit);
    return goodnessOfFit.calculate();
  }

  double[] weights() {
    return weights;
  }

  List<Double> factors() {
    return factors;
  }
}
