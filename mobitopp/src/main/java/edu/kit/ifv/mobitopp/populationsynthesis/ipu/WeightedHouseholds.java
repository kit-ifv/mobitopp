package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class WeightedHouseholds {

	private final List<WeightedHousehold> households;

	@Deprecated
	public WeightedHouseholds(List<WeightedHousehold> households) {
		this.households = households;
	}

	public WeightedHouseholds(WeightedHouseholds other) {
		this.households = new ArrayList<>(other.households);
	}

	@Deprecated
	public List<WeightedHousehold> toList() {
		return households;
	}

	public int size() {
		return households.size();
	}

	public WeightedHouseholds deepCopy() {
		List<WeightedHousehold> copied = households
				.stream()
				.map(WeightedHousehold::new)
				.collect(Collectors.toList());
		return new WeightedHouseholds(copied);
	}

}
