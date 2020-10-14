package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

public interface ArrayIteration {

	ArrayWeightedHouseholds adjustWeightsOf(ArrayWeightedHouseholds households);

	double calculateGoodnessOfFitFor(ArrayWeightedHouseholds households);

}