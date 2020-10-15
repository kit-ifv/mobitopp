package edu.kit.ifv.mobitopp.populationsynthesis.ipu;


public class DefaultArrayIteration implements ArrayIteration {

  @Override
  public WeightedHouseholds adjustWeightsOf(WeightedHouseholds households) {
    return households.scale();
  }

  @Override
  public double calculateGoodnessOfFitFor(WeightedHouseholds households) {
    return households.calculateGoodnessOfFit();
  }

}
