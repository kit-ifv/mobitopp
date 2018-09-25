package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.ToIntFunction;

public class ValueConstraint extends BaseConstraint implements Constraint {

	private final ToIntFunction<WeightedHousehold> value;

	public ValueConstraint(int requestedWeight, ToIntFunction<WeightedHousehold> value) {
		super(requestedWeight);
		this.value = value;
	}

	@Override
	protected boolean matches(WeightedHousehold household) {
		return 0 < value.applyAsInt(household);
	}

	@Override
	protected double totalWeight(WeightedHousehold household) {
		return household.weight() * value.applyAsInt(household);
	}

}
