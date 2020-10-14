package edu.kit.ifv.mobitopp.populationsynthesis.ipu;


public class DefaultArrayIteration implements ArrayIteration {

  @Override
  public ArrayWeightedHouseholds adjustWeightsOf(ArrayWeightedHouseholds households) {
    return households.scale();
  }

  @Override
  public double calculateGoodnessOfFitFor(ArrayWeightedHouseholds households) {
    return households.calculateGoodnessOfFit();
  }

}
