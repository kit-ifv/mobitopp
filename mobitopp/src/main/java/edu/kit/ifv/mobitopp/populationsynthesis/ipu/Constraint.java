package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

public interface Constraint {

	List<WeightedHousehold> scaleWeightsOf(List<WeightedHousehold> households);

	double calculateGoodnessOfFitFor(List<WeightedHousehold> households);

}