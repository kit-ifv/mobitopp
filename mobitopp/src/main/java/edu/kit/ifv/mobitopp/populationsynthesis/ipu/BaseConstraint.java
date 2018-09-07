package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class BaseConstraint implements Constraint {

	private final double requestedWeight;

	public BaseConstraint(double requestedWeight) {
		super();
		this.requestedWeight = requestedWeight;
	}

	@Override
	public List<Household> update(List<Household> households) {
		double totalWeight = totalWeight(households);
		double factor = requestedWeight / totalWeight;
		return update(households, factor);
	}

	private double totalWeight(List<Household> households) {
		return households.stream().filter(this::matches).mapToDouble(this::totalWeight).sum();
	}

	private List<Household> update(List<Household> households, double factor) {
		ArrayList<Household> newHouseholds = new ArrayList<>(notProcessed(households));
		households
				.stream()
				.filter(this::matches)
				.map(h -> h.newWeight(h.weight() * factor))
				.forEach(newHouseholds::add);
		return newHouseholds;
	}

	private List<Household> notProcessed(List<Household> households) {
		Predicate<Household> predicate = this::matches;
		return households.stream().filter(predicate.negate()).collect(toList());
	}
	
	@Override
	public double calculateGoodnessOfFitFor(List<Household> households) {
		double totalWeight = totalWeight(households);
		return Math.abs(totalWeight - requestedWeight) / requestedWeight;
	}

	protected abstract boolean matches(Household household);

	protected abstract double totalWeight(Household household);

}