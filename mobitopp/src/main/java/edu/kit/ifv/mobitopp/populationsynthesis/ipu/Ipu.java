package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;

public class Ipu {

	private final Iteration iteration;
	private final int maxIterations;
	private final double maxGoodness;
	private List<Household> bestHouseholds;
	private double bestGoodness;
	private List<Household> currentHouseholds;

	public Ipu(Iteration iteration, int maxIterations, double maxGoodness) {
		super();
		this.iteration = iteration;
		this.maxIterations = maxIterations;
		this.maxGoodness = maxGoodness;
	}

	public List<Household> adjustWeightsOf(List<Household> households) {
		initialise(households);
		List<Household> lastIterationHouseholds = currentHouseholds;
		for (int current = 0; current < maxIterations; current++) {
			updateHouseholds(lastIterationHouseholds);
			double goodnessOfFit = calculateGoodness();
			if (converged(goodnessOfFit)) {
				return currentHouseholds;
			}
			lastIterationHouseholds = currentHouseholds;
			updateBest(goodnessOfFit);
		}
		return bestHouseholds;
	}

	private void initialise(List<Household> households) {
		currentHouseholds = new ArrayList<>(households);
		bestHouseholds = currentHouseholds;
		bestGoodness = calculateGoodness();
	}

	private void updateHouseholds(List<Household> lastIterationHouseholds) {
		currentHouseholds = iteration.adjustWeightsOf(lastIterationHouseholds);
	}

	private double calculateGoodness() {
		return iteration.calculateGoodnessOfFitFor(currentHouseholds);
	}

	private boolean converged(double goodnessOfFit) {
		return Math.abs(goodnessOfFit - bestGoodness) <= maxGoodness;
	}

	private void updateBest(double goodnessOfFit) {
		if (bestGoodness > goodnessOfFit) {
			bestGoodness = goodnessOfFit;
			bestHouseholds = currentHouseholds;
		}
	}

}
