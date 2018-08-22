package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.ToIntFunction;

public class PersonConstraint extends BaseConstraint implements Constraint {

	private final ToIntFunction<Household> value;

	public PersonConstraint(int requestedWeight, ToIntFunction<Household> value) {
		super(requestedWeight);
		this.value = value;
	}

	@Override
	protected boolean matches(Household household) {
		return 0 < value.applyAsInt(household);
	}

	@Override
	protected double totalWeight(Household household) {
		return household.weight() * value.applyAsInt(household);
	}

}
