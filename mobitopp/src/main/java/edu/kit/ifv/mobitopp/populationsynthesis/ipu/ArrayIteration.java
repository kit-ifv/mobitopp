package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

public interface ArrayIteration {

	WeightedHouseholds adjustWeightsOf(WeightedHouseholds households);

	double calculateGoodnessOfFitFor(WeightedHouseholds households);

}