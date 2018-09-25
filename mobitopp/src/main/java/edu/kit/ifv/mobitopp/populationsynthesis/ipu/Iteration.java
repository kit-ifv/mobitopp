package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

public interface Iteration {

	List<WeightedHousehold> adjustWeightsOf(List<WeightedHousehold> households);

	double calculateGoodnessOfFitFor(List<WeightedHousehold> households);

}