package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

public interface Iteration {

	List<Household> adjustWeightsOf(List<Household> households);

	double calculateGoodnessOfFitFor(List<Household> households);

}