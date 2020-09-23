package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

public interface Constraint {

	WeightedHouseholds scaleWeightsOf(WeightedHouseholds households);

	double calculateGoodnessOfFitFor(WeightedHouseholds households);

}