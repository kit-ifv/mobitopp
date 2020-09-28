package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SimpleConstraint implements Constraint {

	public static final double greaterZero = 1e-6;
	private final double requestedWeight;
  private final String attribute;

	public SimpleConstraint(String attribute, double requestedWeight) {
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
		double totalWeight = totalWeight(households);
		double withFactor = requestedWeight / totalWeight;
		return scaleWeightsOf(households, withFactor);
	}

	private double totalWeight(WeightedHouseholds households) {
		return households.toList().stream().mapToDouble(this::totalWeight).sum();
	}

	private WeightedHouseholds scaleWeightsOf(
			WeightedHouseholds households, double factor) {
		households.toList()
				.stream()
				.filter(this::matches)
				.forEach(h -> h.setWeight(h.weight() * factor));
		return households;
	}

	private boolean matches(WeightedHousehold household) {
	  return 0 < household.attribute(attribute);
	}

  @Override
	public double calculateGoodnessOfFitFor(WeightedHouseholds households) {
		double totalWeight = totalWeight(households);
		return Math.abs(totalWeight - requestedWeight) / requestedWeight;
	}

  protected double totalWeight(WeightedHousehold household) {
  	return household.weight() * household.attribute(attribute);
  }

}