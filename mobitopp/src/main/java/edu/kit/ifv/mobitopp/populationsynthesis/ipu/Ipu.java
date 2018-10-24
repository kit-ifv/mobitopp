package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Logger;

public class Ipu {

	private final Iteration iteration;
	private final int maxIterations;
	private final double maxGoodness;
	private final Logger logger;
	private List<WeightedHousehold> bestHouseholds;
	private double bestGoodness;
	private List<WeightedHousehold> currentHouseholds;
	private int lastIteration;
	private int bestIteration;

	public Ipu(Iteration iteration, int maxIterations, double maxGoodness, Logger logger) {
		super();
		this.iteration = iteration;
		this.maxIterations = maxIterations;
		this.maxGoodness = maxGoodness;
		this.logger = logger;
	}

	public List<WeightedHousehold> adjustWeightsOf(List<WeightedHousehold> households) {
		initialise(households);
		List<WeightedHousehold> lastIterationHouseholds = currentHouseholds;
		for (lastIteration = 0; lastIteration < maxIterations; lastIteration++) {
			updateHouseholds(lastIterationHouseholds);
			double goodnessOfFit = calculateGoodness();
			if (converged(goodnessOfFit)) {
				log(lastIteration, lastIteration, goodnessOfFit);
				return currentHouseholds;
			}
			lastIterationHouseholds = currentHouseholds;
			updateBest(goodnessOfFit);
		}
		log(lastIteration, bestIteration, bestGoodness);
		return bestHouseholds;
	}

	private void log(int lastIteration, int bestIteration, double bestGoodness) {
		logger
				.println(String
						.format("iterations: %s best iteration: %s goodness of fit: %s", lastIteration,
								bestIteration, bestGoodness));
	}

	private void initialise(List<WeightedHousehold> households) {
		currentHouseholds = new ArrayList<>(households);
		bestHouseholds = currentHouseholds;
		bestGoodness = calculateGoodness();
	}

	private void updateHouseholds(List<WeightedHousehold> lastIterationHouseholds) {
		currentHouseholds = iteration.adjustWeightsOf(lastIterationHouseholds);
	}

	private double calculateGoodness() {
		return iteration.calculateGoodnessOfFitFor(currentHouseholds);
	}

	private boolean converged(double goodnessOfFit) {
		boolean improves = goodnessOfFit < bestGoodness;
		boolean belowThreshold = Math.abs(goodnessOfFit - bestGoodness) <= maxGoodness;
		return improves && belowThreshold;
	}

	private void updateBest(double goodnessOfFit) {
		if (bestGoodness > goodnessOfFit) {
			bestGoodness = goodnessOfFit;
			bestHouseholds = currentHouseholds;
		}
	}

}
