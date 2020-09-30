package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ArrayWeightedHouseholds {

  private final List<WeightedHousehold> households;
  private final List<String> attributes;
  private final int[][] values;
  private final double[] weights;

  public ArrayWeightedHouseholds(final List<WeightedHousehold> households) {
    this.households = households;
    this.attributes = collectAttributes(households);
    this.weights = new double[households.size()];
    for (int householdIndex = 0; householdIndex < households.size(); householdIndex++) {
      WeightedHousehold household = households.get(householdIndex);
      weights[householdIndex] = household.weight();
    }
    this.values = new int[households.size()][this.attributes.size()];
    for (int householdIndex = 0; householdIndex < households.size(); householdIndex++) {
      WeightedHousehold household = households.get(householdIndex);
      for (int attributeIndex = 0; attributeIndex < this.attributes.size(); attributeIndex++) {
        String attribute = this.attributes.get(attributeIndex);
        values[householdIndex][attributeIndex] = household.attribute(attribute);
      }
    }
  }

  private static List<String> collectAttributes(List<WeightedHousehold> households) {
    return new ArrayList<>(households
        .stream()
        .map(WeightedHousehold::attributeNames)
        .flatMap(Set::stream)
        .collect(StreamUtils.toSortedSet()));
  }

  public ArrayWeightedHouseholds(final ArrayWeightedHouseholds other) {
    this(new ArrayList<>(other.households));
  }

  public List<WeightedHousehold> toList() {
    for (int householdIndex = 0; householdIndex < values.length; householdIndex++) {
      households.get(householdIndex).setWeight(weights[householdIndex]);
    }
    return households;
  }

  public int size() {
    return households.size();
  }

  public ArrayWeightedHouseholds deepCopy() {
    List<WeightedHousehold> copied = households
        .stream()
        .map(WeightedHousehold::new)
        .collect(Collectors.toList());
    return new ArrayWeightedHouseholds(copied);
  }

  public ArrayWeightedHouseholds scale(String attribute, double requestedWeight) {
    int attributeIndex = this.attributes.indexOf(attribute);
    double totalWeight = totalWeight(attributeIndex);
    double withFactor = requestedWeight / totalWeight;
    return scaleWeights(withFactor, attributeIndex);
  }

  private double totalWeight(int attributeIndex) {
    double newWeight = 0.0d;
    for (int householdIndex = 0; householdIndex < values.length; householdIndex++) {
      newWeight += weights[householdIndex] * values[householdIndex][attributeIndex];
    }
    return newWeight;
  }

  private ArrayWeightedHouseholds scaleWeights(double withFactor, int attributeIndex) {
    for (int householdIndex = 0; householdIndex < values.length; householdIndex++) {
      if (0 != values[householdIndex][attributeIndex]) {
        weights[householdIndex] *= withFactor;
      }
    }
    return this;
  }

}
