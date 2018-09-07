package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;

public class Ipu {

	private final Iteration iteration;
	private final int maxIterations;
	private final double maxGoodness;

	public Ipu(Iteration iteration, int maxIterations, double maxGoodness) {
		super();
		this.iteration = iteration;
		this.maxIterations = maxIterations;
		this.maxGoodness = maxGoodness;
	}

	public List<Household> adjustWeightsOf(List<Household> households) {
		List<Household> updatedHouseholds = new ArrayList<>(households);
		List<Household> lastIterationHouseholds = updatedHouseholds;
		List<Household> bestHouseholds = updatedHouseholds;
		double bestGoodness = iteration.calculateGoodnessOfFitFor(households);
		for (int current = 0; current < maxIterations; current++) {
			updatedHouseholds = iteration.adjustWeightsOf(lastIterationHouseholds);
			double goodnessOfFit = iteration.calculateGoodnessOfFitFor(updatedHouseholds);
			if (Math.abs(goodnessOfFit - bestGoodness) <= maxGoodness) {
				return updatedHouseholds;
			}
			lastIterationHouseholds = updatedHouseholds;
			if (bestGoodness > goodnessOfFit) {
				bestGoodness = goodnessOfFit;
				bestHouseholds = updatedHouseholds;
			}
		}
		return bestHouseholds;
	}

}
