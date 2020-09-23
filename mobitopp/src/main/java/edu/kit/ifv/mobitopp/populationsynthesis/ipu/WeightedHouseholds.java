package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class WeightedHouseholds {

	private final List<WeightedHousehold> households;

	@Deprecated
	public WeightedHouseholds(List<WeightedHousehold> households) {
		this.households = households;
	}

	@Deprecated
	public List<WeightedHousehold> toList() {
		return households;
	}

	public int size() {
		return households.size();
	}
	
}
