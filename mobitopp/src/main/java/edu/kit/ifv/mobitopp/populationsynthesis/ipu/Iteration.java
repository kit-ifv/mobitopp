package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

public interface Iteration {

	WeightedHouseholds adjustWeightsOf(WeightedHouseholds households);

	double calculateGoodnessOfFitFor(WeightedHouseholds households);

}