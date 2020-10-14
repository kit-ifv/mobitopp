package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class ArrayWeightedHouseholds {

  private final List<HouseholdOfPanelData> households;
  private final double[] weights;
  private final Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping;
  private final int[][] householdValues;
  private final List<DemandZone> zones;
  private final List<List<Double>> factors;

  public ArrayWeightedHouseholds(
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

  public ArrayWeightedHouseholds(ArrayWeightedHouseholds other) {
    this(other.households, Arrays.copyOf(other.weights, other.weights.length),
        other.requestedWeightsMapping, other.householdValues, other.zones);
  }
  
  public ArrayWeightedHouseholds clone() {
    return new ArrayWeightedHouseholds(this);
  }

  public ArrayWeightedHouseholds scale() {
    List<Double> factorsOfRound = new LinkedList<>();
    for (RegionalLevel level : List
        .of(RegionalLevel.community, RegionalLevel.district, RegionalLevel.zone)) {
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
          double totalWeight = totalWeight(absoluteAttributeIndex, offset, numberOfWeightsPerPart);
          double withFactor = requestedWeight / totalWeight;
          factorsOfRound.add(withFactor);
          scaleWeights(withFactor, absoluteAttributeIndex, offset, numberOfWeightsPerPart);
        }
        offset += numberOfWeightsPerPart;
      }
    }
    factors.add(factorsOfRound);
    return this;
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

  private void scaleWeights(
      double withFactor, int attributeIndex, int offset, int numberOfWeightsPerPart) {
    for (int weightsIndex = offset; (weightsIndex < offset + numberOfWeightsPerPart)
        && (weightsIndex < weights.length); weightsIndex++) {
      int householdIndex = weightsIndex % numberOfHouseholds();
      if (0 != householdValues[householdIndex][attributeIndex]) {
        weights[weightsIndex] *= withFactor;
      }
    }
  }

  private int numberOfHouseholds() {
    return householdValues.length;
  }

  double[] weights() {
    return weights;
  }

  List<List<Double>> factors() {
    return factors;
  }

  public List<WeightedHousehold> toList() {
    List<WeightedHousehold> newHouseholds = new LinkedList<>();
    for (int index = 0; index < weights.length; index++) {
      HouseholdOfPanelData panelHousehold = households.get(index % numberOfHouseholds());
      String zoneId = zones.get(index / numberOfHouseholds()).getExternalId();
      RegionalContext context = new DefaultRegionalContext(RegionalLevel.zone, zoneId);
      WeightedHousehold newHousehold = new WeightedHousehold(panelHousehold.getId(),
          weights[index], context, panelHousehold);
      newHouseholds.add(newHousehold);
    }
    return newHouseholds;
  }

  public double calculateGoodnessOfFit() {
    List<Double> goodnesses = new ArrayList<>();
    double goodnessOfFit = 0.0d;
    int count = 0;
    for (RegionalLevel level : List
        .of(RegionalLevel.community, RegionalLevel.district, RegionalLevel.zone)) {
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
          double totalWeight = totalWeight(absoluteAttributeIndex, offset, numberOfWeightsPerPart);
          double goody = Math.abs(totalWeight - requestedWeight) / requestedWeight;
          goodnesses.add(goody);
          if (Double.isFinite(goody)) {
            goodnessOfFit += goody;
            count++;
          }
        }
        offset += numberOfWeightsPerPart;
      }
    }
    return goodnessOfFit / count;
  }
}
