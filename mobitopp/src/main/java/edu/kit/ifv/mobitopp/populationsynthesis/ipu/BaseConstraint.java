package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Objects;

public abstract class BaseConstraint implements Constraint {

	public static final double greaterZero = 1e-6;
	private final double requestedWeight;

	public BaseConstraint(double requestedWeight) {
		super();
		this.requestedWeight = ensureGreaterZero(requestedWeight);
	}

	private double ensureGreaterZero(double requestedWeight) {
		if (0.0d >= requestedWeight) {
			System.out.println("RequestedWeight must be greater than zero, but was: " + requestedWeight);
			return greaterZero;
		}
		return requestedWeight;
	}

	@Override
	public WeightedHouseholds scaleWeightsOf(WeightedHouseholds households) {
		double totalWeight = totalWeight(households);
		double withFactor = requestedWeight / totalWeight;
		return scaleWeightsOf(households, withFactor);
	}

	private double totalWeight(WeightedHouseholds households) {
		return households.toList().stream().filter(this::matches).mapToDouble(this::totalWeight).sum();
	}

	private WeightedHouseholds scaleWeightsOf(
			WeightedHouseholds households, double factor) {
		households.toList()
				.stream()
				.filter(this::matches)
				.forEach(h -> h.setWeight(h.weight() * factor));
		return households;
	}

	@Override
	public double calculateGoodnessOfFitFor(WeightedHouseholds households) {
		double totalWeight = totalWeight(households);
		return Math.abs(totalWeight - requestedWeight) / requestedWeight;
	}

	protected abstract boolean matches(WeightedHousehold household);

	protected abstract double totalWeight(WeightedHousehold household);

	@Override
	public int hashCode() {
		return Objects.hash(requestedWeight);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseConstraint other = (BaseConstraint) obj;
		return Double.doubleToLongBits(requestedWeight) == Double
				.doubleToLongBits(other.requestedWeight);
	}

	@Override
	public String toString() {
		return "requestedWeight=" + requestedWeight;
	}

}