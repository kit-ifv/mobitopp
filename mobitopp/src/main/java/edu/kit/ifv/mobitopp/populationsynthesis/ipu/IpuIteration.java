package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.Objects;

public class IpuIteration implements Iteration {

	private final List<Constraint> constraints;

	public IpuIteration(List<Constraint> constraints) {
		super();
		this.constraints = constraints;
	}

	@Override
	public WeightedHouseholds adjustWeightsOf(WeightedHouseholds households) {
		WeightedHouseholds newHouseholds = households;
		for (Constraint constraint : constraints) {
			newHouseholds = constraint.scaleWeightsOf(newHouseholds);
		}
		return newHouseholds;
	}

	@Override
	public double calculateGoodnessOfFitFor(WeightedHouseholds households) {
		return constraints
				.stream()
				.mapToDouble(c -> c.calculateGoodnessOfFitFor(households))
				.filter(goodness -> !Double.isInfinite(goodness))
				.filter(goodness -> !Double.isNaN(goodness))
				.average()
				.orElse(Double.MAX_VALUE);
	}

	@Override
	public int hashCode() {
		return Objects.hash(constraints);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IpuIteration other = (IpuIteration) obj;
		return Objects.equals(constraints, other.constraints);
	}

	@Override
	public String toString() {
		return "IpuIteration [constraints=" + constraints + "]";
	}

}
