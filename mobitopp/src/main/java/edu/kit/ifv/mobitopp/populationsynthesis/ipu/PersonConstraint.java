package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class PersonConstraint extends BaseConstraint implements Constraint {

	private final double requestedWeight;
	private final ToIntFunction<Household> value;

	public PersonConstraint(int requestedWeight, ToIntFunction<Household> value) {
		super();
		this.requestedWeight = requestedWeight;
		this.value = value;
	}

	@Override
	protected double requestedWeight() {
		return requestedWeight;
	}

	@Override
	protected Predicate<Household> constraint() {
		return h -> 0 != value.applyAsInt(h);
	}

	@Override
	protected ToDoubleFunction<Household> totalWeightMapper() {
		return h -> h.weight() * value.applyAsInt(h);
	}

}
