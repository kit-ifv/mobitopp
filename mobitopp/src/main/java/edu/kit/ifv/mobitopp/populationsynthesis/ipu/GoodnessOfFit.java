package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

public class GoodnessOfFit implements WeightedHouseholdsConsumer {

  private double goodnessOfFit;
  private int count;

  public GoodnessOfFit() {
    goodnessOfFit = 0.0d;
    count = 0;
  }

  public void process(
      int offset, int numberOfWeightsPerPart, int absoluteAttributeIndex, double requestedWeight,
      double totalWeight) {
    double goodness = Math.abs(totalWeight - requestedWeight) / requestedWeight;
    if (Double.isFinite(goodness)) {
      goodnessOfFit += goodness;
      count++;
    }
  }

  public double calculate() {
    return goodnessOfFit / count;
  }
}
