package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ListWeightedHouseholds {

	private final List<WeightedHousehold> households;

	@Deprecated
	public ListWeightedHouseholds(List<WeightedHousehold> households) {
		this.households = households;
	}

	public ListWeightedHouseholds(ListWeightedHouseholds other) {
		this.households = new ArrayList<>(other.households);
	}

	@Deprecated
	public List<WeightedHousehold> toList() {
		return households;
	}

	public int size() {
		return households.size();
	}

	public ListWeightedHouseholds deepCopy() {
		List<WeightedHousehold> copied = households
				.stream()
				.map(WeightedHousehold::new)
				.collect(Collectors.toList());
		return new ListWeightedHouseholds(copied);
	}

  public ListWeightedHouseholds scale(String attribute, double requestedWeight) {
    double totalWeight = totalWeight(attribute);
    double withFactor = requestedWeight / totalWeight;
    return scaleWeights(withFactor, attribute);
  }

  double totalWeight(String attribute) {
    return households
        .stream()
        .mapToDouble(household -> this.totalWeight(household, attribute))
        .sum();
  }

  private double totalWeight(WeightedHousehold household, String attribute) {
    return household.weight() * household.attribute(attribute);
  }

  private ListWeightedHouseholds scaleWeights(
      double factor, String attribute) {
    households
        .stream()
        .filter(household -> this.matches(household, attribute))
        .forEach(h -> h.setWeight(h.weight() * factor));
    return this;
  }

  private boolean matches(WeightedHousehold household, String attribute) {
    return 0 < household.attribute(attribute);
  }

}
