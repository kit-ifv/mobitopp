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
		List<Household> lastIteration = updatedHouseholds;
		double lastGoodness = iteration.calculateGoodnessOfFitFor(households);
		for (int current = 0; current < maxIterations; current++) {
			updatedHouseholds = iteration.adjustHouseholdWeights(lastIteration);
			double goodnessOfFit = iteration.calculateGoodnessOfFitFor(updatedHouseholds);
			if (Math.abs(goodnessOfFit - lastGoodness) <= maxGoodness) {
				return updatedHouseholds;
			}
			lastIteration = updatedHouseholds;
			lastGoodness = goodnessOfFit;
		}
		return updatedHouseholds;
	}

}
