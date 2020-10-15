package edu.kit.ifv.mobitopp.populationsynthesis.ipu;


public class GoodnessOfFit {

  private double goodnessOfFit;
  private int count;

  public GoodnessOfFit() {
    goodnessOfFit = 0.0d;
    count = 0;
  }

  public void add(double totalWeight, double requestedWeight) {
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
