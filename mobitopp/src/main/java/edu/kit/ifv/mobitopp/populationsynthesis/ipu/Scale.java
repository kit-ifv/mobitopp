package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Scale implements WeightedHouseholdsConsumer {

  private final double[] weights;
  private final int[][] householdValues;
  private final List<Double> factors;

  public Scale(double[] weights, int[][] householdValues) {
    this.weights = weights;
    this.householdValues = householdValues;
    this.factors = new LinkedList<>();
  }

  @Override
  public void process(
      int offset, int numberOfWeightsPerPart, int absoluteAttributeIndex, double requestedWeight,
      double totalWeight) {
    double withFactor = requestedWeight / totalWeight;
    factors.add(withFactor);
    scaleWeights(withFactor, absoluteAttributeIndex, offset, numberOfWeightsPerPart);
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

  public Collection<Double> factors() {
    return factors;
  }

}
