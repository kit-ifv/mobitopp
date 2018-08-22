package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public abstract class BaseConstraint implements Constraint {

	public BaseConstraint() {
		super();
	}

	@Override
	public List<Household> update(List<Household> households) {
		ArrayList<Household> newHouseholds = new ArrayList<>();
		double totalWeight = totalWeight(households);
		double factor = requestedWeight() / totalWeight;
		households
				.stream()
				.filter(constraint())
				.map(h -> h.newWeight(h.weight() * factor))
				.forEach(newHouseholds::add);
		households.stream().filter(constraint().negate()).forEach(newHouseholds::add);
		return newHouseholds;
	}

	private double totalWeight(List<Household> households) {
		return households.stream().filter(constraint()).mapToDouble(totalWeightMapper()).sum();
	}

	protected abstract double requestedWeight();

	protected abstract Predicate<Household> constraint();

	protected abstract ToDoubleFunction<Household> totalWeightMapper();

}