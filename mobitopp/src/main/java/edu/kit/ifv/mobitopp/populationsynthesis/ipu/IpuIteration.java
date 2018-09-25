package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;

public class IpuIteration implements Iteration {

	private final List<Constraint> constraints;

	public IpuIteration(List<Constraint> constraints) {
		super();
		this.constraints = constraints;
	}

	@Override
	public List<WeightedHousehold> adjustWeightsOf(List<WeightedHousehold> households) {
		List<WeightedHousehold> newHouseholds = new ArrayList<>(households);
		for (Constraint constraint : constraints) {
			newHouseholds = constraint.scaleWeightsOf(newHouseholds);
		}
		return newHouseholds;
	}

	@Override
	public double calculateGoodnessOfFitFor(List<WeightedHousehold> households) {
		return constraints
				.stream()
				.mapToDouble(c -> c.calculateGoodnessOfFitFor(households))
				.average()
				.orElseThrow(() -> new IllegalArgumentException("Could not calculate goodness of fit"));
	}
}
