package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.ArrayList;
import java.util.List;

public class Ipu {

	private final List<Constraint> constraints;

	public Ipu(List<Constraint> constraints) {
		super();
		this.constraints = constraints;
	}

	public List<Household> adjustHouseholdWeights(List<Household> households) {
		List<Household> newHouseholds = new ArrayList<>(households);
		for (Constraint constraint : constraints) {
			newHouseholds = constraint.update(newHouseholds);
		}
		return newHouseholds;
	}
}
