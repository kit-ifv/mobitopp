package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SimpleConstraint implements Constraint {

	public static final double greaterZero = 1e-6;
	private final double requestedWeight;
  private final Attribute attribute;

	public SimpleConstraint(Attribute attribute, double requestedWeight) {
		super();
    this.attribute = attribute;
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
	  return households.scale(attribute.name(), requestedWeight);
	}

  @Override
	public double calculateGoodnessOfFitFor(WeightedHouseholds households) {
		double totalWeight = households.totalWeight(attribute.name());
		return Math.abs(totalWeight - requestedWeight) / requestedWeight;
	}

}