package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.result.Logger;

public class ArrayIpu {

	private final ArrayIteration iteration;
	private final int maxIterations;
	private final double maxGoodness;
	private final Logger logger;
	private WeightedHouseholds bestHouseholds;
	private double bestGoodness;
	private WeightedHouseholds currentHouseholds;
	private int lastIteration;
	private int bestIteration;

	public ArrayIpu(ArrayIteration iteration, int maxIterations, double maxGoodness, Logger logger) {
		super();
		this.iteration = iteration;
		this.maxIterations = maxIterations;
		this.maxGoodness = maxGoodness;
		this.logger = logger;
	}

	public WeightedHouseholds adjustWeightsOf(WeightedHouseholds initialHouseholds) {
		initialise(initialHouseholds);
		WeightedHouseholds lastIterationHouseholds = currentHouseholds;
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

	private void initialise(WeightedHouseholds households) {
		currentHouseholds = new WeightedHouseholds(households);
		bestHouseholds = currentHouseholds;
		bestGoodness = calculateGoodness();
	}

	private void updateHouseholds(WeightedHouseholds lastIterationHouseholds) {
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
			bestHouseholds = currentHouseholds.clone();
		}
	}

}
