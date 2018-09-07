package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

public interface Constraint {

	List<Household> scaleWeightsOf(List<Household> households);

	double calculateGoodnessOfFitFor(List<Household> households);

}